/*
The MIT License (MIT)

Copyright (c) 2016 Tom Needham

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.morganey.editor

import com.intellij.openapi.util.Comparing
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileAdapter
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFilePropertyEvent
import com.intellij.util.FileContentUtilCore

/**
 * Created by thoma on 19/09/2016.
 */
class MorganeyVirtualFileListener : VirtualFileAdapter {
    val component : MorganeyEditorComponent
    constructor(component : MorganeyEditorComponent) : super(){
        this.component = component
    }

    override fun propertyChanged(event : VirtualFilePropertyEvent) {
        super.propertyChanged(event)
        if(VirtualFile.PROP_NAME.equals(event.propertyName)){
            component.updateValidProperty()
            if(Comparing.equal<VirtualFile>(event.file,component.file)
                    && FileContentUtilCore.FORCE_RELOAD_REQUESTOR.equals(event.requestor)
                    || !Comparing.equal<Any?>(event.oldValue,event.newValue)){
                component.updateHighlighters()
            }
        }
    }

    override fun contentsChanged(event : VirtualFileEvent) {
        super.contentsChanged(event)
        if(event.isFromSave){
            MorganeyEditorComponent.assertThread()
            val file : VirtualFile = event.file
            MorganeyEditorComponent.LOG.assertTrue(file.isValid)
            if(component.file!!.equals(file)){
                component.updateModifiedProperty()
            }
        }
    }
}