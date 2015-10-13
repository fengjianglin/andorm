package com.ifatter.andorm;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.android.DexFile;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.ifatter.Andorm;

import dalvik.system.DexClassLoader;

public class AndormExampleActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Andorm.initialize(getApplicationContext());

		// Book book = new Book();
		// book.title = "111";
		// book.url = "http:///andorm/";
		// book.iconUrl = "http:///andorm/icon.png";
		// long i = book.insert();

		// Book.findAll();
		test222();

	}

	void test222() {
		final ClassPool cp = ClassPool.getDefault(getApplicationContext());
		CtClass clsb = null;
		try {
			String className = "com.ifatter.andorm.orm.Model.Book";
			clsb = cp.makeClass(className);
			// clsb.setSuperclass(cp.get(Book.class.getName()));
			CtMethod method = new CtMethod(CtClass.voidType, "test", null, clsb);
			method.setBody("{System.out.println(\"调用了方法：run！！\" );}  ");
			clsb.addMethod(method);

			DexFile dexFile = new DexFile();
			dexFile.addClass(className, clsb.toBytecode());
			dexFile.writeFile(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/1/classes.dex");

			DexClassLoader dexClassLoader = new DexClassLoader(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/1/classes.dex", getDir("odex", 0).getAbsolutePath(),
					null, getClassLoader());

			Class<?> teaClass = dexClassLoader.loadClass(className);
			Object o = teaClass.newInstance();
			Toast.makeText(this, o.toString(), Toast.LENGTH_LONG).show();

			// o.test();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (clsb != null) {
				clsb.defrost();
			}

		}
	}

}
