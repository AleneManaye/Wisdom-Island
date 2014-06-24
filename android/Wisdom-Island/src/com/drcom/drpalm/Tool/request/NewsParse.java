package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.service.ItemDataTranslate;
import com.drcom.drpalm.objs.NewsItem;

public class NewsParse extends BaseParse{
	public NewsParse(HashMap<String, Object> map) {
		super(map);

	}
	public Integer parseTotalResults(){
		Integer ret = 0;
		if(mHashMap.containsKey(NewsDefine.NEWS_TOTALRESULTS)){
			String value = (String)mHashMap.get(NewsDefine.NEWS_TOTALRESULTS);
			ret = ItemDataTranslate.String2Intger(value);
		}
		return ret;
	}
	public Date parseLastModified(){
		Date date = null;
		if(mHashMap.containsKey(NewsDefine.NEWS_LASTMODIFIED)){
			String value = (String)mHashMap.get(NewsDefine.NEWS_LASTMODIFIED);
			date = DateFormatter.getDateFromSecondsString(value);
		}
		return date;
	}
	
	public int parseSearchCount(){
		int count = 0;
		if(mHashMap.containsKey(NewsDefine.NEWS_SEARCH_COUNT)){
			String value = (String)mHashMap.get(NewsDefine.NEWS_SEARCH_COUNT);
			count = Integer.valueOf(value);
		}
		return count;
	}
	
	// 获取新闻详细的返回处理
	public NewsItem parseNewsItem(){
		NewsItem newsItem = null;
		try{
			newsItem = new NewsItem();
			if(mHashMap.containsKey(NewsDefine.NEWS_ID)){
				String value = (String)mHashMap.get(NewsDefine.NEWS_ID);
				newsItem.story_id = ItemDataTranslate.String2Intger(value);
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_STATUS)){
				String value = (String)mHashMap.get(NewsDefine.NEWS_STATUS);
				newsItem.status = value;
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_CATEGORY)){
				String value = (String)mHashMap.get(NewsDefine.NEWS_CATEGORY);
				newsItem.category_id = ItemDataTranslate.String2Intger(value);
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_TITLE)){
				newsItem.title = (String)mHashMap.get(NewsDefine.NEWS_TITLE);
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_SUMMARY)){
				newsItem.summary = (String)mHashMap.get(NewsDefine.NEWS_SUMMARY);
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_AUTHOR)){
				newsItem.author = (String)mHashMap.get(NewsDefine.NEWS_AUTHOR);
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_POST)){
				String value = (String)mHashMap.get(NewsDefine.NEWS_POST);
				newsItem.postDate = DateFormatter.getDateFromSecondsString(value);
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_LASTUPDATE)){
				String value = (String)mHashMap.get(NewsDefine.NEWS_LASTUPDATE);
				newsItem.lastupdate = DateFormatter.getDateFromSecondsString(value);
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_THUMBURL)){
				String value = (String)mHashMap.get(NewsDefine.NEWS_THUMBURL);
				newsItem.thumb_url = value;
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_SHAREURL)){
				String value = (String)mHashMap.get(NewsDefine.NEWS_SHAREURL);
				newsItem.share_url = value;
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_BODY)){
				String value = (String)mHashMap.get(NewsDefine.NEWS_BODY);
				newsItem.body = value;
			}
			if(mHashMap.containsKey(NewsDefine.NEWS_IMAGES)){
				Object otherImageObj = mHashMap.get(NewsDefine.NEWS_IMAGES);
				if(null != otherImageObj){
					// must sure the type of the object, else throw a exception
					String strClass = otherImageObj.getClass().getName();
					if(!String.class.getName().equals(strClass)){
						ArrayList<HashMap<String,Object>> imgsList = (ArrayList<HashMap<String,Object>>)otherImageObj;
						for(int j = 0;j<imgsList.size(); j++){
							HashMap<String,Object> imageMap = imgsList.get(j);
							// add img into image list of item here
							NewsItem.Image img = new NewsItem.Image();
							if(imageMap.containsKey(NewsDefine.NEWS_SMALLURL)){
								String value = (String)imageMap.get(NewsDefine.NEWS_SMALLURL);
								img.smallURL = value;
							}
							if(imageMap.containsKey(NewsDefine.NEWS_FULLURL)){
								String value = (String)imageMap.get(NewsDefine.NEWS_FULLURL);
								img.fullURL = value;
							}
							if(imageMap.containsKey(NewsDefine.NEWS_IMAGECAPTION)){
								String value = (String)imageMap.get(NewsDefine.NEWS_IMAGECAPTION);
								img.imageCaption = value;
							}
							newsItem.otherImgs.add(img);
						}
					}
				}
			}
		}catch(Exception e){

		}


		return newsItem;
	}
	// 获取新闻列表的返回处理
	public ArrayList<NewsItem> parseNewsItems(){
		ArrayList<HashMap<String,Object>> objectList = null;
		ArrayList<NewsItem> newsList = new ArrayList<NewsItem>();
		if(mHashMap.containsKey(NewsDefine.NEWS_ITEM)) {
			try{
				Object obj = mHashMap.get(NewsDefine.NEWS_ITEM);
				if(null != obj){
					String strObjClass = obj.getClass().getName();
					if(!String.class.getName().equals(strObjClass)){
						objectList = (ArrayList<HashMap<String,Object>>)obj;
						for(int i =0;i<objectList.size();i++){
							HashMap<String,Object> objMap = objectList.get(i);
							NewsItem newsItem = new NewsItem();
							if(objMap.containsKey(NewsDefine.NEWS_ID)){
								String value = (String)objMap.get(NewsDefine.NEWS_ID);
								newsItem.story_id = ItemDataTranslate.String2Intger(value);
							}
							if(objMap.containsKey(NewsDefine.NEWS_STATUS)){
								String value = (String)objMap.get(NewsDefine.NEWS_STATUS);
								newsItem.status = value;
							}
							if(objMap.containsKey(NewsDefine.NEWS_CATEGORY)){
								String value = (String)objMap.get(NewsDefine.NEWS_CATEGORY);
								newsItem.category_id = ItemDataTranslate.String2Intger(value);
							}
							if(objMap.containsKey(NewsDefine.NEWS_TITLE)){
								newsItem.title = (String)objMap.get(NewsDefine.NEWS_TITLE);
							}
							if(objMap.containsKey(NewsDefine.NEWS_SUMMARY)){
								newsItem.summary = (String)objMap.get(NewsDefine.NEWS_SUMMARY);
							}
							if(objMap.containsKey(NewsDefine.NEWS_AUTHOR)){
								newsItem.author = (String)objMap.get(NewsDefine.NEWS_AUTHOR);
							}
							if(objMap.containsKey(NewsDefine.NEWS_POST)){
								String value = (String)objMap.get(NewsDefine.NEWS_POST);
								newsItem.postDate = DateFormatter.getDateFromSecondsString(value);
							}
							if(objMap.containsKey(NewsDefine.NEWS_LASTUPDATE)){
								String value = (String)objMap.get(NewsDefine.NEWS_LASTUPDATE);
								newsItem.lastupdate = DateFormatter.getDateFromSecondsString(value)==null?new Date(0):DateFormatter.getDateFromSecondsString(value);
							}
							if(objMap.containsKey(NewsDefine.NEWS_THUMBURL)){
								String value = (String)objMap.get(NewsDefine.NEWS_THUMBURL);
								newsItem.thumb_url = value;
							}
							newsList.add(newsItem);
						}
					}


				}
			}catch(Exception e){
				int a =0;
			}
		}
		return newsList;
	}

	public Date parseNewsCategoryLastUpdate(){
		Date date = null;
		if(mHashMap.containsKey(NewsDefine.NEWS_CATEGORY_LASTUPDATE)){
			String value = (String)mHashMap.get(NewsDefine.NEWS_CATEGORY_LASTUPDATE);
			date = DateFormatter.getDateFromSecondsString(value);
		}
		return date;
	}
}
