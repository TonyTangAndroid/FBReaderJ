/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.library;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;

import org.geometerplus.android.fbreader.config.ConfigShadow;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageManager;

import java.io.File;

public abstract class ZLAndroidApplication extends Application {
	private org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary myLibrary;
	private ConfigShadow myConfig;

	@TargetApi(Build.VERSION_CODES.FROYO)
	private String getExternalCacheDirPath() {
		final File d = getExternalCacheDir();
		if (d != null) {
			d.mkdirs();
			if (d.exists() && d.isDirectory()) {
				return d.getPath();
			}
		}
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// this is a workaround for strange issue on some devices:
		//    NoClassDefFoundError for android.os.AsyncTask
		try {
			Class.forName("android.os.AsyncTask");
		} catch (Throwable t) {
		}

		myConfig = new ConfigShadow(this);
		new ZLAndroidImageManager();
		myLibrary = new org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary(this);

		myConfig.runOnConnect(new Runnable() {
			public void run() {
				if ("".equals(Paths.TempDirectoryOption.getValue())) {
					String dir = null;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
						dir = getExternalCacheDirPath();
					}
					if (dir == null) {
						dir = Paths.mainBookDirectory() + "/.FBReader";
					}
					Paths.TempDirectoryOption.setValue(dir);
				}
			}
		});
	}

	public final org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary library() {
		return myLibrary;
	}
}
