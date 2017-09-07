//  Project name: Workspace
//  File name   : TaskDelegate.java
//
//  Author      : Phuc
//  Created date: 6/6/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.network;


import java.util.*;


public interface TaskDelegate {

    public void taskDidFinish(UUID taskId, int statusCode, Object response);
}
