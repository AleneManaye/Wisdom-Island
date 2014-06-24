package com.drcom.drpalm.Activitys.util;

import android.support.v4.app.Fragment;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 梁炜杰 on 2014/5/27.
 * 读取配置文件使用反射生成Fragment
 */
public class AnimationFragmentUtil {

    public static List<Fragment> loadAnimationFragments(InputStream inputStream) {

        List<Fragment> fragments = new ArrayList<Fragment>();
        Fragment fragment = null;

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(inputStream, "UTF-8");
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if ("fragment".equals(parser.getName())) {
                            String fragmentName = safeNextText(parser);
                            fragment = (Fragment) Class.forName(fragmentName).newInstance();
                            fragments.add(fragment);
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fragments;
    }

    //解决4.0之前Pull的nextText()方法的bug
    private static String safeNextText(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        String result = parser.nextText();
        if (parser.getEventType() != XmlPullParser.END_TAG) {
            parser.nextTag();
        }
        return result;
    }

}
