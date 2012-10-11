package com.ifatter.andorm;

import com.ifatter.andorm.dao.BookDao;
import com.ifatter.andorm.dao.impl.BookDaoImpl;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.DaoFactory;

import dalvik.system.PathClassLoader;
import dalvik.system.VMStack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class AndormExampleActivity extends Activity {

	BookDao bookDao = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		bookDao = DaoFactory.createDao(BookDaoImpl.class, this);

		Book book = new Book();
		book.setTitle("XXXX");
		book.setUrl("http:///andorm/");
		book.setIconUrl("http:///andorm/icon.png");
		bookDao.insert(book);

		book = new Book();
		book.setTitle("YYYY");
		book.setUrl("http://www");
		book.setIconUrl("http://www");
		bookDao.insert(book);

		bookDao.testTransaction();

		test2();

	}

	public void test() {

		XMLHandler handler = new XMLHandler();
		String fileName = "com/ifatter/andorm/database/config.xml";
		ClassLoader loader = VMStack.getCallingClassLoader();

		System.out.println(loader.toString());

		InputStream stream = loader.getResourceAsStream(fileName);
		if (stream != null) {
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser parser = spf.newSAXParser();
				parser.parse(stream, handler);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		System.out.println("---::" + handler.packageName);
	}

	public void test2() {

		XMLHandler handler = new XMLHandler();
		String fileName = "com/ifatter/andorm/database/config.xml";
		ClassLoader loader = VMStack.getCallingClassLoader();

		String apkPath = loader.toString();
		int start = apkPath.indexOf('[');
		int end = apkPath.indexOf(']', start);
		apkPath = apkPath.substring(start + 1, end);
		System.out.println("----" + apkPath);

		File file = new File(apkPath);
		long size = file.length();
		System.out.println("-----:" + size);

		InputStream stream = loader.getResourceAsStream(fileName);
		if (stream != null) {
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser parser = spf.newSAXParser();
				parser.parse(stream, handler);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		System.out.println("---::" + handler.packageName);
	}

	static class XMLHandler extends DefaultHandler {

		public String packageName = null;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if ("manifest".equals(localName)) {
				packageName = attributes.getValue("package");
			}
		}

	}

}
