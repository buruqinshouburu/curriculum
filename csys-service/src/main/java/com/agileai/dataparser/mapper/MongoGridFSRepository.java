package com.agileai.dataparser.mapper;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MongoGridFSRepository {

    private static final String ID = "_id";

    private Logger logger = LoggerFactory.getLogger(MongoGridFSRepository.class);

    @Autowired
    private GridFsTemplate gridFsTemplate;

    /**
     * 存储多个文件
     * @param files
     * @return 逗号连接的objectId
     * @throws Exception
     */
    public String store(List<File> files){
        List<String> objectIds = new ArrayList<>();
        files.forEach(file->objectIds.add(this.store(file)));
        return objectIds.stream().collect(Collectors.joining(","));
    }


    /**
     * 存储多个文件
     * @param files
     * @return 逗号连接的objectId
     * @throws Exception
     */
    public String store(File[] files){
        List<String> objectIds = new ArrayList<>();
        Arrays.stream(files).forEach(file->objectIds.add(this.store(file)));
        return objectIds.stream().collect(Collectors.joining(","));
    }

    /**
     * 存储单个文件
     * @param file
     * @return objectId 字符串
     * @throws Exception
     */
    public String store(File file) {
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("文件找不到："+file.getAbsolutePath());
        }
        return this.store(is,file.getName());
    }

    /**
     * 存储单个文件
     * @param is
     * @param name
     * @return objectId 字符串
     * @throws Exception
     */
    public String store(InputStream is, String name) {
        return gridFsTemplate.store(is,name).toString();
    }

    /**
     * 删除单个文件
     * @param id
     * @return 1
     * @throws Exception
     */
    public int delete(String id) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
        return 1;
    }

    /**
     * 删除多个文件
     * @param ids
     * @return 1
     * @throws Exception
     */
    public int delete(List<String> ids) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").in(ids)));
        return 1;
    }


    public GridFSFile find(ObjectId objectId){
        return gridFsTemplate.findOne(Query.query(Criteria.where(ID).is(objectId)));
    }

    public GridFSFile find(String objectId){
        return find(new ObjectId(objectId));
    }

    public InputStream getFileInputStream(String id){
        GridFSFile gridFSFile = find(new ObjectId(id));
        GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public InputStream getFileInputStream(GridFSFile gridFSFile){
        GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

//    public File getFile(String id){
//        GridFSFile gridFSFile = find(new ObjectId(id));
//        GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
//        try {
//            return resource.getFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        InputStream inputStream = null;
//        File file = new File(getAbsoluteFile(gridFSFile.getFilename()));
//        FileOutputStream fileOutputStream = null;
//        try {
//            inputStream = resource.getInputStream();
//            fileOutputStream = new FileOutputStream(getAbsoluteFile(gridFSFile.getFilename()));
//            byte[] bytes = new byte[1024];
//            while(inputStream.read(bytes) > 0){
//                fileOutputStream.write(bytes);
//            }
//            fileOutputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                file.delete();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                fileOutputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return file;
//    }


}
