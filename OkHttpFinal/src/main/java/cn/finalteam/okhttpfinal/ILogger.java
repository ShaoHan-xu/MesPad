/*
 * Copyright (C) 2015 彭建波(pengjianbo@finalteam.cn), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.finalteam.okhttpfinal;

import android.util.Log;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2016/2/2 0002 12:49
 */
class ILogger {
    public static final String DEFAULT_TAG = "OkHttpFinal";
    protected static boolean DEBUG = BuildConfig.DEBUG;

    public static void d(String message) {
        Log.d(DEFAULT_TAG, message);
    }

    public static void e(Throwable throwable) {
        throwable.printStackTrace();
    }

    public static void e(String message, Object... args) {
        Log.e(message, args.toString());
    }

    public static void e(Throwable throwable, String message, Object... args) {
//        Log.e(throwable, message, args);
    }

    public static void i(String message, Object... args) {
        Log.i(message, args.toString());
    }

    public static void v(String message, Object... args) {
        Log.v(message, args.toString());
    }

    public static void w(String message, Object... args) {
        Log.w(message, args.toString());
    }

    public static void wtf(String message, Object... args) {
        Log.wtf(message, args.toString());
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
//        Log.json(json);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public static void xml(String xml) {
        Log.d(DEFAULT_TAG, xml);
    }
}
