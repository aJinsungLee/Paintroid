/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.paintroid.test.junit.command;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;
import at.tugraz.ist.paintroid.test.junit.stubs.BaseCommandStub;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;

public class BaseCommandTest extends AndroidTestCase {

	private BaseCommandStub mBaseCommand;
	// protected PrivateAccess PrivateAccess = new PrivateAccess();
	private Bitmap mBitmap;

	public BaseCommandTest() {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mBaseCommand = new BaseCommandStub();
		mBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		PrivateAccess.setMemberValue(BaseCommand.class, mBaseCommand, "mBitmap", mBitmap);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		mBitmap.recycle();
		mBitmap = null;
	}

	@Test
	public void testBaseCommand() {
		try {
			BaseCommandStub testCommand = new BaseCommandStub(null);
			testCommand = new BaseCommandStub(new Paint());
		} catch (Exception e) {
			fail("EXCETIPN: failed with uninitialised Objects" + e.toString());
		}
	}

	@Test
	public void testFreeResources() {
		File cacheDir = PaintroidApplication.APPLICATION_CONTEXT.getCacheDir();
		File storedBitmap = new File(cacheDir.getAbsolutePath(), "test");
		try {
			assertFalse(storedBitmap.exists());

			PrivateAccess.setMemberValue(BaseCommand.class, mBaseCommand, "mFileToStoredBitmap", storedBitmap);
			mBaseCommand.freeResources();
			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mBitmap"));

			File restoredBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand,
					"mFileToStoredBitmap");

			assertFalse("bitmap not deleted", restoredBitmap.exists());
			if (restoredBitmap.exists())
				restoredBitmap.delete();
		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		}

		try {
			storedBitmap.createNewFile();
			assertTrue(storedBitmap.exists());
			mBaseCommand.freeResources();
			assertFalse(storedBitmap.exists());
			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mBitmap"));
		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		}

	}

	@Test
	public void testStoreBitmap() {
		File storedBitmap = null;
		try {
			PrivateAccess.setMemberValue(BaseCommand.class, mBaseCommand, "mFileToStoredBitmap", storedBitmap);
			mBaseCommand.storeBitmapStub();
			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mBitmap"));

			storedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mFileToStoredBitmap");
			assertNotNull(storedBitmap);
			assertNotNull(storedBitmap.getAbsolutePath());
			Bitmap restoredBitmap = BitmapFactory.decodeFile(storedBitmap.getAbsolutePath());
			PaintroidAsserts.assertBitmapEquals(restoredBitmap, mBitmap);

		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		} finally {
			if (storedBitmap != null) {
				if (storedBitmap.delete() == false)
					fail("Failed to delete the stored bitmap(0)");
			} else {
				fail("Failed to delete the stored bitmap(1)");
			}

		}

	}
}