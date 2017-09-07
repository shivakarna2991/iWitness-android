//  Project name: Workspace
//  File name   : FwiDownloadService.java
//
//  Author      : Phuc
//  Created date: 6/16/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.perpcast.lib.services;


import com.perpcast.lib.services.request.*;
import org.apache.http.*;

import java.io.*;

public class FwiDownloadService extends FwiService {


    private String _path;


    // Class's constructors
    public FwiDownloadService(FwiRequest request, String path) {
        super(request);
        this._path = path;
    }


    // Class's public methods
    @Override
    public String getResource() {
        super.execute();

        if (_res != null) {
            HttpEntity entity   = _res.getEntity();
            InputStream  input  = null;
            OutputStream output = null;
            String filename     = null;

            // Download data
            try {
                byte[] bytes = new byte[512];
                input  = entity.getContent();
                output = new FileOutputStream(_path);

                // Download Data
                int length = 0;
                while ((length = input.read(bytes)) > 0) {
                    output.write(bytes, 0, length);
                }

                // Close connection
                input.close();
                output.close();

                filename = _req.getURI().toString();
            }
            catch (Exception ex) {
                _req.abort();

                // Close input
                if (input != null) {
                    input.close();
                }

                // Close output
                if (output != null) {
                    output.close();
                }

                // Reset cacheFile
                filename = null;
            }
            finally {
                return filename;
            }
        }
        else {
            return _req.getURI().toString();
        }
    }
}
