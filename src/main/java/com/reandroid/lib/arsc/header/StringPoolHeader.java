 /*
  *  Copyright (C) 2022 github.com/REAndroid
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package com.reandroid.lib.arsc.header;

import com.reandroid.lib.arsc.chunk.ChunkType;
import com.reandroid.lib.arsc.item.IntegerItem;
import com.reandroid.lib.arsc.item.ShortItem;

public class StringPoolHeader extends HeaderBlock{
    private final IntegerItem countStrings;
    private final IntegerItem countStyles;
    private final ShortItem flagUtf8;
    private final ShortItem flagSorted;
    private final IntegerItem startStrings;
    private final IntegerItem startStyles;
    public StringPoolHeader() {
        super(ChunkType.STRING.ID);
        this.countStrings = new IntegerItem();
        this.countStyles = new IntegerItem();
        this.flagUtf8 = new ShortItem();
        this.flagSorted = new ShortItem();
        this.startStrings = new IntegerItem();
        this.startStyles = new IntegerItem();

        addChild(countStrings);
        addChild(countStyles);
        addChild(flagUtf8);
        addChild(flagSorted);
        addChild(startStrings);
        addChild(startStyles);
    }
    public IntegerItem getCountStrings() {
        return countStrings;
    }
    public IntegerItem getCountStyles() {
        return countStyles;
    }
    public ShortItem getFlagUtf8() {
        return flagUtf8;
    }
    public ShortItem getFlagSorted() {
        return flagSorted;
    }
    public IntegerItem getStartStrings() {
        return startStrings;
    }
    public IntegerItem getStartStyles() {
        return startStyles;
    }
    @Override
    public String toString(){
        if(getChunkType()!=ChunkType.STRING){
            return super.toString();
        }
        return getClass().getSimpleName()
                +" {strings="+getCountStrings()
                +", styles="+getCountStyles()
                +", utf8="+getFlagUtf8().toHex()
                +", sorted="+getFlagSorted().toHex()
                +", offset-strings="+getStartStrings().get()
                +", offset-styles="+getStartStyles().get() + '}';
    }
}
