package com.drcom.drpalm.objs;

import java.util.ArrayList;
import java.util.Date;

public class NewsItem {
	public static String STATUS_TYPE_N =  "N";	//Normal
	public static String STATUS_TYPE_C =  "C";	//Cancel
	public static String STATUS_TYPE_D =  "D";	//Del
	
	public NewsItem() {
		otherImgs = new ArrayList<Image>();
	}
	
	public int story_id = -1;
	public String status = "";
	public String title = "";
	public String summary = "";
	public String author = "";
	public Date postDate = new Date(2000, 1, 1);
	public Date lastupdate = new Date(2000, 1, 1);
	public String thumb_url = "";
	public String front_cover_small = "";
	public String front_cover_full = "";
	public String body = "";
	public int category_id = -1;
	public String share_url = "";
	
	public ArrayList<Image> otherImgs = new ArrayList<NewsItem.Image>();
	
	public boolean isRead = false;//是否已读
	public boolean bookmark = false ;//收藏标记
	
	public static class Image {
		public String smallURL = "";
		public String fullURL  = "";
		public String imageCaption = "";
	}
}

/*
http://mobile-dev.mit.edu/api/newsoffice/

	science (which happens to be channel_id=2):
	http://mobile-dev.mit.edu/api/newsoffice/?channel_id=2
	
	to get more results for the category
	http://mobile-dev.mit.edu/api/newsoffice/?channel_id=2&story_id=15391
	where story_id means return results older than this story_id, each
	story has a unique Id

	a news search:
	http://web.mit.edu/newsoffice/index.php?option=com_search&view=isearch&searchword=fun&ordering=newest&limit=10&start=0

	to get more search results:
	http://web.mit.edu/newsoffice/index.php?option=com_search&view=isearch&searchword=fun&ordering=newest&limit=10&start=10
		
		
/*
<?xml version="1.0" encoding="utf-8"?>
<rss version="2.0">
<channel>
<title/>
<link>http://web.mit.edu/newsoffice</link>
<description/>

<item>
<title><![CDATA[Researchers seek to put the squeeze on cancer]]></title>
<author><![CDATA[Anne Trafton, MIT News Office]]></author>
<category>1</category>
<link>http://web.mit.edu/newsoffice/2010/angiogenesis-06152010.html</link>
<story_id>15438</story_id>
<featured>1</featured>
<description><![CDATA[Cell contractions may be key to initiating new blood-vessel growth near tumors.]]></description>
<postDate>Tue, 15 Jun 2010 04:00:00 EDT </postDate>

<image>
<thumbURL>http://web.mit.edu/newsoffice/images/article_images/w76/20100611145614-1.png</thumbURL>
<smallURL width="140" height="105">http://web.mit.edu/newsoffice/images/article_images/w140/20100611145614-1.jpg</smallURL>
<fullURL width="368" height="276">http://web.mit.edu/newsoffice/images/article_images/20100611145614-1.jpg</fullURL>
<imageCaption><![CDATA[MIT and Tufts researchers have shown that mechanical forces from cells that surround small blood vessels may control the growth of new vessels.]]>
</imageCaption>
</image>

<body><![CDATA[Cancer researchers have been s
/>]]></body>
</item></channel></rss>
*/
