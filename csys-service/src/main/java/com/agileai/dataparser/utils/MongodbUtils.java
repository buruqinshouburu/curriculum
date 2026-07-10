package com.agileai.dataparser.utils;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MongodbUtils {

	private static final Logger logger = LoggerFactory.getLogger(MongodbUtils.class);

	private static final String DB_MG_URI="mongodb://192.168.1.252:27019";
	private static final String DB_MG_DB="agileai_dataparser_dev";

	private MongoClient mongoClient = null;
	private MongoDatabase database = null;


	private static Map<String, MongoCollection<Document>> dbCollectionMap = new ConcurrentHashMap<String, MongoCollection<Document>>();

	public MongodbUtils(String mongoUrl, String mongoDatabase) {
		logger.info("开始构建MongoDB访问类...");

//		mongoClient = MongoClients.create(new ConnectionString(DB_MG_URI));
//		database = mongoClient.getDatabase(DB_MG_DB);
		mongoClient = MongoClients.create(new ConnectionString(mongoUrl));
		database = mongoClient.getDatabase(mongoDatabase);
        logger.info("[mongodb.uri={}],[mongodb.db={}]",mongoUrl,mongoDatabase);
		logger.info("完成构建MongoDB访问类...");
	}

	private static class holder{

	}
//	private static final MongodbUtils INSTANCE=new MongodbUtils();

//	public static final MongodbUtils getInstance(){
//		return INSTANCE;
//	}





	public MongoCollection<Document> getDBCollection(String collectionName) {
		MongoCollection<Document> collection = null;
		if (dbCollectionMap.containsKey(collectionName)) {
			collection = dbCollectionMap.get(collectionName);
		} else {
			collection = database.getCollection(collectionName);
			if (null != collection) {
				dbCollectionMap.put(collectionName, collection);
			}
		}
		return collection;
	}

	public void close(){
		if (mongoClient!=null){
			mongoClient.close();
		}
	}



//	public Map<Integer,String> selectDictName(Integer maxId){
//
//		CountDownLatch flag = new CountDownLatch(1);
//		Map<Integer,String> dicMap=new LinkedHashMap<Integer,String>();
//
//		getDBCollection(Constants.TABLE_TYPEDICTIONARY).find(Filters.gt(Constants.FIELD_ID, maxId/*,Filters.eq(Constants.FIELD_TASKTYPE, 1)*/))
//		.sort(Sorts.descending(Constants.FIELD_ID))
//		.projection(Projections.include(Constants.FIELD_ID,Constants.FIELD_VALUE))/*.limit(1000)*/
//		.forEach((Document document)->{
//			Integer id=document.getInteger(Constants.FIELD_ID);
//			String name=document.getString(Constants.FIELD_VALUE);
//			dicMap.put(id, name);
//		},(Void result, final Throwable exception) ->{
//			if (exception!=null){
//				logger.error("[selectDict]获取字典基础失败，异常如下：{}",exception);
//			}
//			flag.countDown();
//		});
//
//
//		try {
//			flag.await();
//		} catch (InterruptedException e) {
//			logger.error("[selectDict]获取字典基础失败，异常如下：{}",e);
//		}
//
//		return dicMap;
//	}
//
//
//	public Map<Integer,String> selectSiteNameDic(Integer maxId){
//
//		CountDownLatch flag = new CountDownLatch(1);
//		Map<Integer,String> dicMap=new LinkedHashMap<Integer,String>();
//
//		getDBCollection(Constants.TABLE_SITE).find(Filters.gt(Constants.FIELD_ID, maxId/*,Filters.eq(Constants.FIELD_TASKTYPE, 1)*/))
//		.sort(Sorts.descending(Constants.FIELD_ID))
//		.projection(Projections.include(Constants.FIELD_ID,Constants.FIELD_NAME))/*.limit(1000)*/
//		.forEach((Document document)->{
//			Integer id=document.getInteger(Constants.FIELD_ID);
//			String name=document.getString(Constants.FIELD_NAME);
//			dicMap.put(id, name);
//		},(Void result, final Throwable exception) ->{
//			if (exception!=null){
//				logger.error("[selectSiteDic]获取站点基础信息失败，异常如下：{}",exception);
//			}
//			flag.countDown();
//		});
//
//
//		try {
//			flag.await();
//		} catch (InterruptedException e) {
//			logger.error("[selectDict]获取站点基础信息失败，异常如下：{}",e);
//		}
//
//		return dicMap;
//	}
//
//	public Map<Integer,String> selectChannelNameDic(Integer maxId){
//
//		CountDownLatch flag = new CountDownLatch(1);
//		Map<Integer,String> dicMap=new LinkedHashMap<Integer,String>();
//		Map<Integer,Integer> channelIdToChannelTypeIdMap=new LinkedHashMap<Integer,Integer>();
//
//		getDBCollection(Constants.TABLE_CHANNEL).find(Filters.gt(Constants.FIELD_ID, maxId/*,Filters.eq(Constants.FIELD_TASKTYPE, 1)*/))
//		.sort(Sorts.descending(Constants.FIELD_ID))
//		.projection(Projections.include(Constants.FIELD_ID,Constants.FIELD_NAME))/*.limit(1000)*/
//		.forEach((Document document)->{
//			Integer channelId=document.getInteger(Constants.FIELD_ID);
//			String channelName=document.getString(Constants.FIELD_NAME);
//			dicMap.put(channelId, channelName);
//
//			Integer channelTypeId=document.getInteger(Constants.FIELD_TYPE);
//			channelIdToChannelTypeIdMap.put(channelId, channelTypeId);
//
//		},(Void result, final Throwable exception) ->{
//			if (exception!=null){
//				logger.error("[selectSiteDic]获取站点基础信息失败，异常如下：{}",exception);
//			}
//			flag.countDown();
//		});
//
//
//		try {
//			flag.await();
//		} catch (InterruptedException e) {
//			logger.error("[selectDict]获取站点基础信息失败，异常如下：{}",e);
//		}
//
//		return dicMap;
//	}
//
//
//	public Map<Integer,String> selectSiteIdToSiteDomainUrl(Integer maxId){
//
//		CountDownLatch flag = new CountDownLatch(1);
//		Map<Integer,String> siteIdToSiteDomainUrlMap=new LinkedHashMap<Integer,String>();
//
//		getDBCollection(Constants.TABLE_SITE).find(Filters.gt(Constants.FIELD_ID, maxId/*,Filters.eq(Constants.FIELD_TASKTYPE, 1)*/))
//		.sort(Sorts.descending(Constants.FIELD_ID))
//		.projection(Projections.include(Constants.FIELD_ID,Constants.FIELD_URL))/*.limit(1000)*/
//		.forEach((Document document)->{
//			Integer siteId=document.getInteger(Constants.FIELD_ID);
//			String url=document.getString(Constants.FIELD_URL);
//			siteIdToSiteDomainUrlMap.put(siteId, url);
//		},(Void result, final Throwable exception) ->{
//			if (exception!=null){
//				logger.error("[selectSiteIdToSiteDomainUrl]获取站点类型基础数据失败，异常如下：{}",exception);
//			}
//			flag.countDown();
//		});
//
//
//		try {
//			flag.await();
//		} catch (InterruptedException e) {
//			logger.error("[selectSiteIdToSiteDomainUrl]获取任务失败，异常如下：{}",e);
//		}
//
//		return siteIdToSiteDomainUrlMap;
//	}
//
//
//
//	public Map<Integer,Integer> selectSiteIdToSiteTypeId(Integer maxId){
//
//		CountDownLatch flag = new CountDownLatch(1);
//		Map<Integer,Integer> siteIdToSiteTypeIdMap=new LinkedHashMap<Integer,Integer>();
//
//		getDBCollection(Constants.TABLE_SITE).find(Filters.gt(Constants.FIELD_ID, maxId/*,Filters.eq(Constants.FIELD_TASKTYPE, 1)*/))
//		.sort(Sorts.descending(Constants.FIELD_ID))
//		.projection(Projections.include(Constants.FIELD_ID,Constants.FIELD_ORGTYPE))/*.limit(1000)*/
//		.forEach((Document document)->{
//			Integer siteId=document.getInteger(Constants.FIELD_ID);
//			Integer siteTypeId=document.getInteger(Constants.FIELD_ORGTYPE);
//			siteIdToSiteTypeIdMap.put(siteId, siteTypeId);
//		},(Void result, final Throwable exception) ->{
//			if (exception!=null){
//				logger.error("[selectSiteIdToSiteTypeId]获取站点类型基础数据失败，异常如下：{}",exception);
//			}
//			flag.countDown();
//		});
//
//
//		try {
//			flag.await();
//		} catch (InterruptedException e) {
//			logger.error("[selectSiteIdToSiteTypeId]获取任务失败，异常如下：{}",e);
//		}
//
//		return siteIdToSiteTypeIdMap;
//	}
//
//
//	public List<ContentTask> selectContentTask(Integer workday){
//
//		CountDownLatch flag = new CountDownLatch(1);
//		List<ContentTask> taskList=new ArrayList<ContentTask>();
//		getDBCollection(Constants.TABLE_CONTENTTASK+Constants.UNDERLINE+workday).find(Filters.eq(Constants.FIELD_STATE, Constants.STATE_DOWNLOAD))
//		.projection(Projections.include(
//				Constants.FIELD_ID,Constants.FIELD_TASKID,
//				Constants.FIELD_TASKTYPE,Constants.FIELD_WORKDAY,
//
//				Constants.FIELD_SITEID,Constants.FIELD_CHANNELID,
//				Constants.FIELD_CHANNELTYPEID,Constants.FIELD_DOWNLOAD_TYPE,
//				Constants.FIELD_CHANNELURL,Constants.FIELD_URL,
//
//				Constants.FIELD_CREATETIME,Constants.FIELD_STATE,
//
//
//				Constants.FIELD_TASKTYPE,Constants.FIELD_SITEID,
//				Constants.FIELD_TYPE,Constants.FIELD_ENCODING,
//				Constants.FIELD_CONTENTTYPE,Constants.FIELD_URL,
//				Constants.FIELD_REQUESTMETHOD,Constants.FIELD_REQUESTTIMEOUT,
//				Constants.FIELD_REQUESTRETRYTIME,Constants.FIELD_REQUESTRETRYCOUNT,
//				Constants.FIELD_URLMATCHREGEX,Constants.FIELD_CRAWLERCYCLE,
//				Constants.FIELD_ALLCOUNT,Constants.FIELD_ERRCOUNT, Constants.FIELD_DOWNLOAD_TYPE)
//				)
//		.forEach((Document document)->{
//			try {
//				taskList.add(new ContentTask(document));
//			} catch (Exception e) {
//				logger.error("[selectContentTask]查询数据出错，异常如下：",e);
//			}
//		},(Void result, final Throwable exception) ->{
//			if (exception!=null){
//				logger.error("[selectContentTask]获取新任务失败，异常如下：{}",exception);
//			}
//			flag.countDown();
//		});
//
//
//		try {
//			flag.await();
//		} catch (InterruptedException e) {
//			logger.error("[selectContentTask]获取任务失败，异常如下：{}",e);
//		}
//
//		return taskList;
//	}
//
//
//	public List<IndexTask> selectChannel(){
//
//		DateTime now=DateTime.now();
//		String workdayString=now.toString(Constants.DTF_YYYYMMDD);
//		Integer workday=Integer.valueOf(workdayString);
//
//		CountDownLatch flag = new CountDownLatch(1);
//		List<IndexTask> taskList=new ArrayList<IndexTask>();
//		getDBCollection(Constants.TABLE_CHANNEL).find(Filters.and(Filters.lte(Constants.FIELD_NEXTCRAWLERTIME, new Date()),Filters.eq(Constants.FIELD_STATE, 1)/*,Filters.eq(Constants.FIELD_ID, 21715)/*,Filters.eq(Constants.FIELD_TASKTYPE, 1)*/))
//		//.limit(1)
//
//		.sort(Sorts.ascending(Constants.FIELD_NEXTCRAWLERTIME))
//		.projection(Projections.include(
//				Constants.FIELD_ID,Constants.FIELD_NAME,
//				Constants.FIELD_TASKTYPE,Constants.FIELD_SITEID,
//				Constants.FIELD_TYPE,Constants.FIELD_ENCODING,
//				Constants.FIELD_CONTENTTYPE,Constants.FIELD_URL,
//				Constants.FIELD_REQUESTMETHOD,Constants.FIELD_REQUESTTIMEOUT,
//				Constants.FIELD_REQUESTRETRYTIME,Constants.FIELD_REQUESTRETRYCOUNT,
//				Constants.FIELD_URLMATCHREGEX,Constants.FIELD_CRAWLERCYCLE,
//				Constants.FIELD_ALLCOUNT,Constants.FIELD_ERRCOUNT, Constants.FIELD_DOWNLOAD_TYPE))
//		.forEach((Document document)->{
//			try {
//				taskList.add(new IndexTask(document,workday));
//			} catch (Exception e) {
//				logger.error("[selectChannel]查询数据出错，异常如下：",e);
//				//e.printStackTrace();
//				//System.out.println("id:...."+document.getInteger(Constants.FIELD_ID));
//			}
//
//		},(Void result, final Throwable exception) ->{
//			if (exception!=null){
//				logger.error("[selectChannel]获取新任务失败，异常如下：{}",exception);
//			}
//			flag.countDown();
//		});
//
//
//		try {
//			flag.await();
//		} catch (InterruptedException e) {
//			logger.error("[selectChannel]获取任务失败，异常如下：{}",e);
//		}
//
//		return taskList;
//	}
//
//
//
//	public void updateChannel(Map<IndexTask,Map<String,Object>> updateChannel){
//		if (updateChannel==null || updateChannel.isEmpty()){
//			return;
//		}
//
//	    List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>(updateChannel.size());
//	    updateChannel.forEach((task,result)->{
//			Document value=new Document();
//			Document update=new Document(Constants.DB_MG_SET,value);
//			result.forEach((k,v)->{
//				value.append(k,v);
//			});
//
//			UpdateOneModel<Document>  updateResult = new UpdateOneModel<Document>(Filters.eq(Constants.FIELD_ID, task.getChannelId()),update);
//			requests.add(updateResult);
//	    });
//		getDBCollection(Constants.TABLE_CHANNEL).bulkWrite(requests,
//			(final BulkWriteResult updateResult, final Throwable exception) -> {
//		    	if (exception!=null){
//		    		logger.error("[updateChannel]批量更新栏目信息失败，异常如下：{}",exception);
//		    	}
//		});
//
//	}
//
//	public void updateChannel(IndexTask task,Map<String,Object> result){
//		Document value=new Document();
//		Document update=new Document(Constants.DB_MG_SET,value);
//		result.forEach((k,v)->{
//			value.append(k,v);
//		});
//		getDBCollection(Constants.TABLE_CHANNEL)
//			.updateOne(Filters.eq(Constants.FIELD_ID, task.getChannelId()), update,
//			(final UpdateResult updateResult, final Throwable exception) -> {
//		    	if (exception!=null){
//		    		logger.error("[updateChannel]更新频道信息失败，异常如下：{}",exception);
//		    	}
//			});
//	}
//
//
//
//
//	public void insertIndexTask(IndexTask task){
//		Document doc=task.toMongoDBObject();
//
//		getDBCollection(task.getTaskTable()).insertOne(doc, (Void result, final Throwable exception) -> {
//	    	if (exception!=null){
//	    		logger.error("[insertIndexTask]保存索引任务失败，异常如下：{}",exception);
//	    	}
//		});
//	}
//
//	public void insertIndexTask(List<IndexTask> tasks){
//		if (tasks==null || tasks.isEmpty()){
//			return;
//		}
//
//		List<Document> docs=new ArrayList<Document>();
//		tasks.forEach(task->{
//			docs.add(task.toMongoDBObject());
//		});
//		String taskTable=tasks.iterator().next().getTaskTable();
//
//		getDBCollection(taskTable).insertMany(docs, (Void result, final Throwable exception) -> {
//	    	if (exception!=null){
//	    		logger.error("[insertIndexTask]批量保存索引任务失败，异常如下：{}",exception);
//	    	}
//		});
//	}
//
//
//
//	public void insertContentTask(ContentTask task){
//		Document doc = task.toMongoDBObject();
//
//		getDBCollection(task.getTaskTable()).insertOne(doc, (Void result, final Throwable exception) -> {
//	    	if (exception!=null){
//	    		logger.error("[insertContentTask]保存内容任务失败，异常如下：{}",exception);
//	    	}
//		});
//
//	}
//
//
//
//
//	public void insertContentTask(List<ContentTask> tasks){
//		if (tasks==null || tasks.isEmpty()){
//			return;
//		}
//		List<Document> docs=new ArrayList<Document>(tasks.size());
//		tasks.forEach(task->{
//			docs.add(task.toMongoDBObject());
//		});
//		String taskTable=tasks.iterator().next().getTaskTable();
//
//		getDBCollection(taskTable).insertMany(docs, (Void result, final Throwable exception) -> {
//	    	if (exception!=null){
//	    		logger.error("[insertContentTask]批量保存内容任务失败，异常如下：{}",exception);
//	    	}
//		});
//	}
//
//
//	public void insertInfoResult(ContentTask task){
//		Document doc = task.toResultObject();
//
//		getDBCollection(task.getResultTable()).insertOne(doc, (Void result, final Throwable exception) -> {
//	    	if (exception!=null){
//	    		logger.error("[insertInfoResult]保存结果失败，异常如下：{}",exception);
//	    	}
//		});
//
//		/*
//		 collection.insertOne(doc, new SingleResultCallback<Void>() {
//		    @Override
//		    public void onResult(final Void result, final Throwable t) {
//		        System.out.println("Inserted!");
//		    }
//		});
//		 */
//	}
//
//	public void duplicateUrl(List<ContentTask> contentTaskList){
//		if (contentTaskList==null || contentTaskList.isEmpty()){
//			return;
//		}
//		Map<String,ContentTask> urlRefContentTask=new HashMap<String,ContentTask>();
//		contentTaskList.forEach(contentTask->{
//			String urlMD5=contentTask.getUrlMD5();
//			urlRefContentTask.put(urlMD5, contentTask);
//		});
//
//		//CompletableFuture<Boolean> flag = new CompletableFuture<>();
//		CountDownLatch flag = new CountDownLatch(1);
//
//		getDBCollection(Constants.TABLE_URLS).find(Filters.in(Constants.FIELD_ID, urlRefContentTask.keySet())).forEach(
//		(Document document)->{
//			String id=document.getString(Constants.FIELD_ID);
//			if (StringUtils.isBlank(id)){
//				return;
//			}
//			urlRefContentTask.remove(id);
//		},(Void result, final Throwable exception) ->{
//			if (exception!=null){
//				logger.error("[duplicateUrl]URL去重查询失败，异常如下：{}",exception);
//			}
//			flag.countDown();
//		});
//
//		try {
//			flag.await();
//		} catch (InterruptedException e) {
//			logger.error("[duplicateUrl]URL去重查询失败，异常如下：{}",e);
//		}
//		contentTaskList.clear();
//
//		if (urlRefContentTask.isEmpty()){
//			return;
//		}
//		contentTaskList.addAll(urlRefContentTask.values());
//
//		List<Document> docs=new ArrayList<Document>(urlRefContentTask.size());
//		urlRefContentTask.keySet().forEach(urlMd5->{
//			docs.add(new Document(Constants.FIELD_ID,urlMd5));
//		});
//
//		getDBCollection(Constants.TABLE_URLS).insertMany(docs, (Void result, final Throwable exception) -> {
//	    	if (exception!=null){
//	    		logger.error("[duplicateUrl]批量保存新URL失败，异常如下：{}",exception);
//	    	}
//		});
//	}
//
//
//	public void updateIndexTask(IndexTask task,Map<String,Object> result){
//		Document value=new Document();
//		Document update=new Document(Constants.DB_MG_SET,value);
//		result.forEach((k,v)->{
//			value.append(k,v);
//		});
//		getDBCollection(task.getTaskTable())
//			.updateOne(Filters.eq(Constants.FIELD_ID, task.getId()), update,
//			(final UpdateResult updateResult, final Throwable exception) -> {
//		    	if (exception!=null){
//		    		logger.error("[updateIndexTask]更新索引任务失败，异常如下：{}",exception);
//		    	}
//			});
//	}
//
//	public void updateIndexTask(IndexTask task,List<Map<String,Object>> result){
//
//	    List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>(result.size());
//	    result.forEach(r->{
//			Document value=new Document();
//			Document update=new Document(Constants.DB_MG_SET,value);
//			r.forEach((k,v)->{
//				value.append(k,v);
//			});
//			UpdateOneModel<Document>  updateResult = new UpdateOneModel<Document>(Filters.eq(Constants.FIELD_ID, task.getId()),update);
//			requests.add(updateResult);
//	    });
//		getDBCollection(task.getTaskTable()).bulkWrite(requests,
//			(final BulkWriteResult updateResult, final Throwable exception) -> {
//		    	if (exception!=null){
//		    		logger.error("[updateIndexTask]批量更新索引任务失败，异常如下：{}",exception);
//		    	}
//		});
//	}
//
//
//	public void updateContentTask(ContentTask task,Map<String,Object> result){
//
//		Document value=new Document();
//		Document update=new Document(Constants.DB_MG_SET,value);
//		result.forEach((k,v)->{
//			value.append(k,v);
//		});
//		getDBCollection(task.getTaskTable()).updateOne(Filters.eq(Constants.FIELD_ID, task.getId()), update,
//			(final UpdateResult updateResult, final Throwable exception) -> {
//		    	if (exception!=null){
//		    		logger.error("[updateContentTask]更新内容任务失败，异常如下：{}",exception);
//		    	}
//		});
//	}
//
//	public void updateContentTask(ContentTask task,List<Map<String,Object>> result){
//
//	    List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>(result.size());
//	    result.forEach(r->{
//			Document update=new Document();
//			r.forEach((k,v)->{
//				update.append(Constants.DB_MG_SET,new Document(k,v));
//			});
//			UpdateOneModel<Document>  updateResult = new UpdateOneModel<Document>(Filters.eq(Constants.FIELD_ID, task.getId()),update);
//			requests.add(updateResult);
//	    });
//
//		getDBCollection(task.getTaskTable()).bulkWrite(requests,
//			(final BulkWriteResult updateResult, final Throwable exception) -> {
//
//				logger.info("[updateContentTask]批量更新内容任务{}条，完成更新{}条",updateResult.getModifiedCount(),result.size());
//
//		    	if (exception!=null){
//		    		logger.error("[updateContentTask]批量更新内容任务结果失败，异常如下：{}",exception);
//		    	}
//			});
//	}



}
