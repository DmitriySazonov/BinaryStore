package com.binarystore.adapter;

import com.binarystore.adapter.boxed.BooleanBinaryAdapter;
import com.binarystore.adapter.boxed.ByteBinaryAdapter;
import com.binarystore.adapter.boxed.CharBinaryAdapter;
import com.binarystore.adapter.boxed.DoubleBinaryAdapter;
import com.binarystore.adapter.boxed.FloatBinaryAdapter;
import com.binarystore.adapter.boxed.IntBinaryAdapter;
import com.binarystore.adapter.boxed.LongBinaryAdapter;
import com.binarystore.adapter.boxed.ShortBinaryAdapter;
import com.binarystore.adapter.collection.lists.ArrayListBinaryAdapter;
import com.binarystore.adapter.collection.lists.LinkedListBinaryAdapter;
import com.binarystore.adapter.collection.lists.ListBinaryAdapter;
import com.binarystore.adapter.collection.lists.SimpleBinaryLazyListAdapter;
import com.binarystore.adapter.collection.lists.StackBinaryAdapter;
import com.binarystore.adapter.collection.lists.VectorBinaryAdapter;
import com.binarystore.adapter.collection.queues.ArrayDequeBinaryAdapter;
import com.binarystore.adapter.collection.queues.PriorityQueueBinaryAdapter;
import com.binarystore.adapter.collection.queues.QueueBinaryAdapter;
import com.binarystore.adapter.collection.sets.HashSetBinaryAdapter;
import com.binarystore.adapter.collection.sets.LinkedHashSetBinaryAdapter;
import com.binarystore.adapter.collection.sets.SetBinaryAdapter;
import com.binarystore.adapter.collection.sets.TreeSetBinaryAdapter;
import com.binarystore.adapter.map.ConcurrentHashMapBinaryAdapter;
import com.binarystore.adapter.map.ConcurrentSkipListMapBinaryAdapter;
import com.binarystore.adapter.map.EnumMapBinaryAdapter;
import com.binarystore.adapter.map.HashMapBinaryAdapter;
import com.binarystore.adapter.map.LinkedHashMapBinaryAdapter;
import com.binarystore.adapter.map.MapBinaryAdapter;
import com.binarystore.adapter.map.TreeMapBinaryAdapter;
import com.binarystore.collections.SimpleBinaryLazyList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class BasicBinaryAdapters {

    public static void registerInto(AdapterFactoryRegister register) {
        register.register(Character.class, CharBinaryAdapter.factory);
        register.register(Boolean.class, BooleanBinaryAdapter.factory);
        register.register(Byte.class, ByteBinaryAdapter.factory);
        register.register(Short.class, ShortBinaryAdapter.factory);
        register.register(Integer.class, IntBinaryAdapter.factory);
        register.register(Long.class, LongBinaryAdapter.factory);
        register.register(Float.class, FloatBinaryAdapter.factory);
        register.register(Double.class, DoubleBinaryAdapter.factory);

        register.register(String.class, StringBinaryAdapter.factory);

        register.register(TreeMap.class, TreeMapBinaryAdapter.factory);
        register.register(HashMap.class, HashMapBinaryAdapter.factory);
        register.register(LinkedHashMap.class, LinkedHashMapBinaryAdapter.factory);
        register.register(ConcurrentHashMap.class, ConcurrentHashMapBinaryAdapter.factory);
        register.register(ConcurrentSkipListMap.class, ConcurrentSkipListMapBinaryAdapter.factory);
        register.register(EnumMap.class, EnumMapBinaryAdapter.factory);
        register.register(Map.class, MapBinaryAdapter.factory);


        register.register(Class.class, ClassBinaryAdapter.factory);
        register.register(Enum.class, EnumBinaryAdapter.factory);

        register.register(ArrayList.class, ArrayListBinaryAdapter.factory);
        register.register(LinkedList.class, LinkedListBinaryAdapter.factory);
        register.register(Stack.class, StackBinaryAdapter.factory);
        register.register(Vector.class, VectorBinaryAdapter.factory);
        register.register(ArrayDeque.class, ArrayDequeBinaryAdapter.factory);
        register.register(PriorityQueue.class, PriorityQueueBinaryAdapter.factory);
        register.register(HashSet.class, HashSetBinaryAdapter.factory);
        register.register(LinkedHashSet.class, LinkedHashSetBinaryAdapter.factory);
        register.register(TreeSet.class, TreeSetBinaryAdapter.factory);
        register.register(List.class, ListBinaryAdapter.factory);
        register.register(Set.class, SetBinaryAdapter.factory);
        register.register(Queue.class, QueueBinaryAdapter.factory);
        register.register(SimpleBinaryLazyList.class, SimpleBinaryLazyListAdapter.factory);
    }
}
