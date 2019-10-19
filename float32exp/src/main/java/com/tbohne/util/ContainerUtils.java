package com.tbohne.util;

import java.util.ArrayList;
import java.util.Collections;

public class ContainerUtils {

    //These would be generic, but Resource implements Comparable<ResourceBase>, which screws everything up
//    public static int InsertSorted(ArrayList<ResourceCappable> list, ResourceCappable object)
//    {
//        int pos = Collections.binarySearch(list, object);
//        if (pos < 0) {
//            list.add(-pos - 1, object);
//            return -pos - 1;
//        } else
//            return pos;
//    }

    public static int InsertSorted(ArrayList list, Comparable object)
    {
        int pos = Collections.binarySearch(list, object);
        if (pos < 0) {
            list.add(-pos - 1, object);
        }
        return -pos - 1;
    }

//    public static <T extends Comparable> int InsertSorted(ArrayAdapter<T> list, T object)
//    {
//        int count = list.getCount();
//        int offset=0;
//        for( ; offset<count; ++offset) {
//            if (list.getItem(offset).compareTo(object)>0)
//                break;
//        }
//        list.insert(object, offset);
//        return offset;
//    }

    public static <T extends Comparable> void assertSorted(ArrayList<T> list, int aroundIndex)
    {
        int min = Math.max(0, aroundIndex-2);
        int max = Math.min(list.size()-1, aroundIndex + 1);
        for(int i=min; i<max; ++i) {
            T left = list.get(i);
            T right = list.get(i+1);
            if (left.compareTo(right) >= 0)
                throw new IllegalStateException(left + " and " + right + " are out of order!");
        }
    }
}
