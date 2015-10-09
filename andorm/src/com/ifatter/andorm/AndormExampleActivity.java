package com.ifatter.andorm;

import java.io.FileOutputStream;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.file.ClassDefItem;
import com.android.dx.dex.file.DexFile;
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
			clsb = cp.makeClass("Tea");
			CtMethod method = new CtMethod(CtClass.voidType, "run", null, clsb);
			method.setBody("{System.out.println(\"调用了方法：run！！\" );}  ");
			clsb.addMethod(method);

			DexOptions dexOptions = new DexOptions();
			DexFile dexFile = new DexFile(dexOptions);

			ClassDefItem classDefItem = CfTranslator.translate("Tea.class",
					clsb.toBytecode(), new CfOptions(), dexOptions);
			dexFile.add(classDefItem);
			dexFile.writeTo(new FileOutputStream(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/1/classes.dex"), null, false);

			DexClassLoader dexClassLoader = new DexClassLoader(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/1/classes.dex", getDir("osdk", 0).getAbsolutePath(),
					null, getClassLoader());

			Class<?> teaClass = dexClassLoader.loadClass("Tea");
			Object o = teaClass.newInstance();
			Toast.makeText(this, o.toString(), Toast.LENGTH_LONG).show();

			teaClass.getDeclaredMethod("run").invoke(o);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (clsb != null) {
				clsb.defrost();
			}

		}
	}

}
