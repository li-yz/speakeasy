package utils;

import utils.MyPrint;

import java.io.*;

/**
 * Created by Liyanzhen on 2016/12/28.
 */
public class MySerialization {
    public void serializeObject(Object object, String path){
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(object);

            oos.flush();
            oos.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
            MyPrint.print("序列化重叠社区发现结果失败");
        }
    }

    public Object antiSerializeObject(String path ){
        Object object = null;
        try{
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            object = ois.readObject();

            ois.close();
            fis.close();

        }catch (IOException e){
            e.printStackTrace();
            MyPrint.print("反序列化重叠社区发现结果异常！！！");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return object;
    }
}
