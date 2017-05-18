/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//httpclient/src/java/org/apache/commons/httpclient/methods/multipart/MultipartRequestEntity.java,v 1.1 2004/10/06 03:39:59 mbecke Exp $
 * $Revision: 502647 $
 * $Date: 2007-02-02 17:22:54 +0100 (Fri, 02 Feb 2007) $
 *
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package com.anson.acode.multipart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;

import com.anson.acode.ALog;
import com.anson.acode.HttpUtilsAndroid.HttpProgressListener;

public class ProgressMultipartEntity extends MultipartEntity {
	
	private static final String TAG = "ProgressMultipartEntity";

    public ProgressMultipartEntity(Part[] parts) {
		super(parts);
		// TODO Auto-generated constructor stub
	}
    
    HttpProgressListener proLis;
    long progress = 0;
    long size = 0;
    public ProgressMultipartEntity(Part[] parts, long size, HttpProgressListener progressListener){
    	super(parts);
    	this.size = size;
    	proLis = progressListener;
    }
    
    @Override
    public void writeTo(OutputStream out) throws IOException {
    	// TODO Auto-generated method stub
    	//ALog.d("writeTo");
    	super.writeTo(proLis == null ? out : new ProgressOutputStream(out, proLis));
    }
    
    class ProgressOutputStream extends FilterOutputStream {

		public ProgressOutputStream(OutputStream out, HttpProgressListener proLis) {
			super(out);
			// TODO Auto-generated constructor stub
		}
    	@Override
    	public void write(byte[] buffer) throws IOException {
    		// TODO Auto-generated method stub
    		super.write(buffer);
    		progress += buffer.length;
    		if(proLis != null)proLis.onProgress(progress, size);
    	}
    	@Override
    	public void write(byte[] buffer, int offset, int length)
    			throws IOException {
    		// TODO Auto-generated method stub
    		super.write(buffer, offset, length);
    		progress += length;
    		if(proLis != null)proLis.onProgress(progress, size);
    	}

        @Override
        public void write(int oneByte) throws IOException {
            super.write(oneByte);
        }
    }
}
