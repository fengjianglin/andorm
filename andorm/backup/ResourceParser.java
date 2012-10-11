/* 
 * Copyright (C) 2011-2012 ifatter.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ifatter.andorm.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.WeakHashMap;
import dalvik.system.VMStack;

public abstract class ResourceParser {

    protected ResourceParser parent;

    private Locale locale;

    static class MissingBundle extends ResourceParser {
        @Override
        public Enumeration<String> getKeys() {
            return null;
        }

        @Override
        public Object handleGetObject(String name) {
            return null;
        }
    }

    private static final ResourceParser MISSING = new MissingBundle();

    private static final ResourceParser MISSINGBASE = new MissingBundle();

    private static final WeakHashMap<Object, Hashtable<String, ResourceParser>> cache = new WeakHashMap<Object, Hashtable<String, ResourceParser>>();

    public static final ResourceParser getBundle(String base) throws MissingResourceException {
        if (base != null) {
            Locale locale = Locale.getDefault();
            ClassLoader loader = VMStack.getCallingClassLoader();
            String localeName = locale.toString();
            if (localeName.length() > 0) {
                localeName = "_" + localeName;
            }
            ResourceParser bundle = handleGetBundle(base, localeName, loader);
            if (bundle != null) {
                return bundle;
            }
            throw new MissingResourceException(null, base + '_' + locale, "");
        }
        throw new NullPointerException();
    }

    public abstract Enumeration<String> getKeys();

    public Locale getLocale() {
        return locale;
    }

    public final Object getObject(String key) {
        ResourceParser last, theParent = this;
        do {
            Object result = theParent.handleGetObject(key);
            if (result != null) {
                return result;
            }
            last = theParent;
            theParent = theParent.parent;
        } while (theParent != null);
        throw new MissingResourceException(null, last.getClass().getName(), key);
    }

    public final String getString(String key) {
        return (String)getObject(key);
    }

    public final String[] getStringArray(String key) {
        return (String[])getObject(key);
    }

    private static ResourceParser handleGetBundle(String base, String locale,
            final ClassLoader loader) {
        ResourceParser bundle = null;
        String bundleName = base + locale;
        Object cacheKey = (loader != null) ? (Object)loader : (Object)"null";
        Hashtable<String, ResourceParser> loaderCache;
        synchronized (cache) {
            loaderCache = cache.get(cacheKey);
            if (loaderCache == null) {
                loaderCache = new Hashtable<String, ResourceParser>(13);
                cache.put(cacheKey, loaderCache);
            }
        }
        ResourceParser result = loaderCache.get(bundleName);
        if (result != null) {
            if (result == MISSINGBASE) {
                return null;
            }
            if (result == MISSING) {
                String extension = strip(locale);
                if (extension == null) {
                    return null;
                }
                return handleGetBundle(base, extension, loader);
            }
            return result;
        }

        if (bundle == null) {
            final String fileName = bundleName.replace('.', '/');
            System.out.println("-fileName:" + fileName);
            InputStream stream = AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
                public InputStream run() {
                    return (loader == null) ? ClassLoader.getSystemResourceAsStream(fileName
                            + ".properties") : loader.getResourceAsStream(fileName + ".properties");
                }
            });
            if (stream != null) {
                try {
                    try {
                        bundle = new PropertyResourceBundle(stream);
                    } finally {
                        stream.close();
                    }
                    bundle.setLocale(locale);
                } catch (IOException e) {
                }
            }
        }

        String extension = strip(locale);
        if (bundle != null) {
            if (extension != null) {
                ResourceParser parent = handleGetBundle(base, extension, loader);
                if (parent != null) {
                    bundle.setParent(parent);
                }
            }
            loaderCache.put(bundleName, bundle);
            return bundle;
        }

        if (extension != null) {
            bundle = handleGetBundle(base, extension, loader);
            if (bundle != null) {
                loaderCache.put(bundleName, bundle);
                return bundle;
            }
        }
        loaderCache.put(bundleName, MISSINGBASE);
        return null;
    }

    protected abstract Object handleGetObject(String key);

    protected void setParent(ResourceParser bundle) {
        parent = bundle;
    }

    private static String strip(String name) {
        int index = name.lastIndexOf('_');
        if (index != -1) {
            return name.substring(0, index);
        }
        return null;
    }

    private void setLocale(String name) {
        String language = "", country = "", variant = "";
        if (name.length() > 1) {
            int nextIndex = name.indexOf('_', 1);
            if (nextIndex == -1) {
                nextIndex = name.length();
            }
            language = name.substring(1, nextIndex);
            if (nextIndex + 1 < name.length()) {
                int index = nextIndex;
                nextIndex = name.indexOf('_', nextIndex + 1);
                if (nextIndex == -1) {
                    nextIndex = name.length();
                }
                country = name.substring(index + 1, nextIndex);
                if (nextIndex + 1 < name.length()) {
                    variant = name.substring(nextIndex + 1, name.length());
                }
            }
        }
        locale = new Locale(language, country, variant);
    }
}
