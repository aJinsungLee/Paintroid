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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;
import at.tugraz.ist.paintroid.command.implementation.BitmapCommand;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;

public class BitmapCommandTest extends CommandTestSetup {
	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mCommandUnderTest = new BitmapCommand(mBitmapUnderTest);
		mCommandUnderTestNull = new BitmapCommand(null);
		mCanvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR - 10);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testRunInsertNewBitmap() {
		Bitmap hasToBeTransparentBitmap = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		hasToBeTransparentBitmap.eraseColor(Color.DKGRAY);
		Bitmap bitmapToCompare = mBitmapUnderTest.copy(Config.ARGB_8888, false);
		try {

			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mFileToStoredBitmap"));

			mCommandUnderTest.run(mCanvasUnderTest, hasToBeTransparentBitmap);

			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mBitmap"));
			PaintroidAsserts.assertBitmapEquals(mCanvasBitmapUnderTest, bitmapToCompare);
			File fileToStoredBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
					"mFileToStoredBitmap");
			assertNotNull(fileToStoredBitmap);
			assertTrue(fileToStoredBitmap.length() > 0);

			fileToStoredBitmap.delete();

		} catch (Exception e) {
			fail("Failed to replace new bitmap:" + e.toString());
		} finally {
			if (hasToBeTransparentBitmap != null) {
				hasToBeTransparentBitmap.recycle();
				hasToBeTransparentBitmap = null;
			}

			if (bitmapToCompare != null) {
				bitmapToCompare.recycle();
				bitmapToCompare = null;
			}

		}
	}

	@Test
	public void testRunReplaceBitmapFromFileSystem() {
		Bitmap bitmapToCompare = mBitmapUnderTest.copy(Config.ARGB_8888, false);
		try {
			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mFileToStoredBitmap"));

			mCommandUnderTest.run(mCanvasUnderTest, null);
			assertNotNull(PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mFileToStoredBitmap"));

			mCanvasBitmapUnderTest.eraseColor(Color.TRANSPARENT);
			mCommandUnderTest.run(mCanvasUnderTest, null);

			PaintroidAsserts.assertBitmapEquals(bitmapToCompare, mCanvasBitmapUnderTest);

		} catch (Exception e) {
			fail("Failed to restore bitmap from file system" + e.toString());
		} finally {
			if (bitmapToCompare != null) {
				bitmapToCompare.recycle();
				bitmapToCompare = null;
			}
		}
	}

	@Test
	public void testBitmapCommand() {
		try {
			assertEquals(mBitmapUnderTest,
					PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mBitmap"));
			PaintroidAsserts.assertBitmapEquals(mBitmapUnderTest,
					(Bitmap) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mBitmap"));
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}

}