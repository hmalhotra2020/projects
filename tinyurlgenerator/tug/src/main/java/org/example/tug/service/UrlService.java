package org.example.tug.service;

import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class UrlService {

    final private String charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    final private int maxLength = charSet.length();
    final private Map<Long, Pair> store = new ConcurrentHashMap<>();
    final private AtomicLong salt = new AtomicLong(1111);

    public UrlService() {}

    public String getTinyUrl(String originalUrl)    {
        String tinyUrl = "";
        long counter = 0l;

        //do{
            counter = getUniqueNumber();
            tinyUrl = convertAndGetBase62Code(counter);
        //}while(tinyUrl.length() > 0 && this.store.containsValue(tinyUrl));
        Pair pair = Pair.with(tinyUrl, originalUrl);
        this.store.put(counter, pair);
        return tinyUrl;
    }

    public String getOriginalUrl(String tinyUrl)    {
        long tinyUrlCode = convertToBase10(tinyUrl);
        String originalUrl = this.store.get(tinyUrlCode).getValue1().toString();
        return originalUrl;
    }

    public String convertAndGetBase62Code(final long counter) {
        StringBuffer sb = new StringBuffer();
        long newNumber = counter;
        while(newNumber>0)    {
            BigInteger remainder = BigInteger.valueOf(newNumber % maxLength);
            sb.append(charSet.charAt(remainder.intValueExact()));
            newNumber = newNumber / maxLength;
        }
        return sb.reverse().toString();
    }

    private long getUniqueNumber() {
        long newNumber = salt.incrementAndGet() + (System.currentTimeMillis()/1000);
        log.info("Salt: {}", newNumber);
        return newNumber;
    }

    public long convertToBase10(String tinyUrl)   {
        long num = 0;
        char[] chars = tinyUrl.toCharArray();
        for(int i=0; i<chars.length; i++)    {
            if ('a' <= tinyUrl.charAt(i) &&
                    tinyUrl.charAt(i) <= 'z')
                num = num * 62 + tinyUrl.charAt(i) - 'a';
            if ('A' <= tinyUrl.charAt(i) &&
                    tinyUrl.charAt(i) <= 'Z')
                num = num * 62 + tinyUrl.charAt(i) - 'A' + 26;
            if ('0' <= tinyUrl.charAt(i) &&
                    tinyUrl.charAt(i) <= '9')
                num = num * 62 + tinyUrl.charAt(i) - '0' + 52;
        }
        return num;
    }

    public String getOriginalUrl2(String tinyUrl)    {
        long tinyUrlCode = convertToBase10_2(tinyUrl);
        String originalUrl = this.store.get(tinyUrlCode).getValue1().toString();
        return originalUrl;
    }

    public long convertToBase10_2(String tinyUrl)   {
        int i = 0, result = 0, counter = 0;
        while(i<tinyUrl.length())   {
            counter = i+1;
            int mapped = this.charSet.indexOf(tinyUrl.charAt(i));
            result += mapped * Math.pow(maxLength, tinyUrl.length()-counter);
            i++;
        }
        return result;
    }

}
