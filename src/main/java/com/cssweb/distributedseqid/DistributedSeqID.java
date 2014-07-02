package com.cssweb.distributedseqid;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by chenhf on 2014/7/2.
 */
public class DistributedSeqID {

    public void getWithdrawID()
    {

    }

    public void getTopupID()
    {

    }

    public void getTradeId()
    {

    }

    public void getOrderID()
    {

    }

    private void getUUID()
    {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        System.out.println("uuid=" + id + ", len=" + id.length());
    }

    public static void main(String args[])
    {


        for (int i=0; i<10; i++) {
            DistributedSeqID id = new DistributedSeqID();
            id.getUUID();

            System.currentTimeMillis(); // 毫秒

            Long nanoTime = System.nanoTime(); //纳秒
            System.out.println("nanoTime" + nanoTime);

            System.out.println("=" + Long.toHexString(nanoTime));



            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS"); // 17 + (32-17)=15
            Date now = new Date();
            String s = sdf.format(now);
System.out.println(s);




        }
    }
}
