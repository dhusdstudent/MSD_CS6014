package MSDNS;

import java.util.HashMap;

public class DNSCache {

    private final HashMap<DNSQuestion, DNSRecord> cache = new HashMap<>();

    public DNSRecord lookup(DNSQuestion question){
        DNSRecord record = cache.get(question);
        if(record == null){
            return null;
        }
        if (record.isExpired()) {
            cache.remove(question);
            return null;
        }
        return record;
    }

    public void insert(DNSQuestion question, DNSRecord record){
        cache.put(question, record);
    }
}
