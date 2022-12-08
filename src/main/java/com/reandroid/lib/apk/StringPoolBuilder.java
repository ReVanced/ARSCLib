package com.reandroid.lib.apk;

import com.reandroid.lib.arsc.chunk.PackageBlock;
import com.reandroid.lib.arsc.chunk.TableBlock;
import com.reandroid.lib.arsc.pool.SpecStringPool;
import com.reandroid.lib.arsc.pool.TableStringPool;
import com.reandroid.lib.json.JSONArray;
import com.reandroid.lib.json.JSONException;
import com.reandroid.lib.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class StringPoolBuilder {
    private final Map<Byte, Set<String>> mSpecNameMap;
    private final Set<String> mTableStrings;
    private byte mCurrentPackageId;
    private JSONArray mStyledStrings;
    public StringPoolBuilder(){
        this.mSpecNameMap = new HashMap<>();
        this.mTableStrings = new HashSet<>();
    }
    public void apply(TableBlock tableBlock){
        applyTableString(tableBlock.getTableStringPool());
        for(byte pkgId:mSpecNameMap.keySet()){
            PackageBlock packageBlock=tableBlock.getPackageArray().getOrCreate(pkgId);
            applySpecString(packageBlock.getSpecStringPool());
        }
    }
    private void applyTableString(TableStringPool stringPool){
        stringPool.fromJson(mStyledStrings);
        stringPool.addStrings(getTableString());
        stringPool.refresh();
    }
    private void applySpecString(SpecStringPool stringPool){
        byte pkgId= (byte) stringPool.getPackageBlock().getId();
        stringPool.addStrings(getSpecString(pkgId));
        stringPool.refresh();
    }
    public void scanDirectory(File resourcesDir) throws IOException {
        mCurrentPackageId=0;
        List<File> pkgDirList=ApkUtil.listDirectories(resourcesDir);
        for(File dir:pkgDirList){
            File pkgFile=new File(dir, ApkUtil.PACKAGE_JSON_FILE);
            scanFile(pkgFile);
            List<File> jsonFileList=ApkUtil.recursiveFiles(dir, ".json");
            for(File file:jsonFileList){
                if(file.equals(pkgFile)){
                    continue;
                }
                scanFile(file);
            }
        }
    }
    public void scanFile(File jsonFile) throws IOException {
        try{
            FileInputStream inputStream=new FileInputStream(jsonFile);
            JSONObject jsonObject=new JSONObject(inputStream);
            build(jsonObject);
        }catch (JSONException ex){
            throw new IOException(jsonFile+": "+ex.getMessage());
        }
    }
    public void build(JSONObject jsonObject){
        scan(jsonObject);
    }
    public Set<String> getTableString(){
        return mTableStrings;
    }
    public Set<String> getSpecString(byte pkgId){
        return mSpecNameMap.get(pkgId);
    }
    private void scan(JSONObject jsonObject){
        if(jsonObject.has("is_array")){
            addSpecName(jsonObject.optString("name"));
        }
        if(jsonObject.has("value_type")){
            if("STRING".equals(jsonObject.getString("value_type"))){
                String data= jsonObject.getString("data");
                addTableString(data);
            }
            return;
        }else if(jsonObject.has(PackageBlock.NAME_package_id)){
            mCurrentPackageId= (byte) jsonObject.getInt(PackageBlock.NAME_package_id);
        }
        Set<String> keyList = jsonObject.keySet();
        for(String key:keyList){
            Object obj=jsonObject.get(key);
            if(obj instanceof JSONObject){
                scan((JSONObject) obj);
                continue;
            }
            if(obj instanceof JSONArray){
                JSONArray jsonArray = (JSONArray) obj;
                if(TableBlock.NAME_styled_strings.equals(key)){
                    this.mStyledStrings = jsonArray;
                }else {
                    scan(jsonArray);
                }
            }
        }
    }
    private void scan(JSONArray jsonArray){
        if(jsonArray==null){
            return;
        }
        for(Object obj:jsonArray.getArrayList()){
            if(obj instanceof JSONObject){
                scan((JSONObject) obj);
                continue;
            }
            if(obj instanceof JSONArray){
                scan((JSONArray) obj);
            }
        }
    }
    private void addTableString(String name){
        if(name==null){
            return;
        }
        mTableStrings.add(name);
    }
    private void addSpecName(String name){
        if(name==null){
            return;
        }
        byte pkgId=mCurrentPackageId;
        if(pkgId==0){
            throw new IllegalArgumentException("Current package id is 0");
        }
        Set<String> specNames=mSpecNameMap.get(pkgId);
        if(specNames==null){
            specNames=new HashSet<>();
            mSpecNameMap.put(pkgId, specNames);
        }
        specNames.add(name);
    }
}
