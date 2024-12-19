//package io.hhplus.tdd.service;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.function.Supplier;
//import org.springframework.stereotype.Service;
//
//@Service
//public class LockService {
//
//  // ID별 Lock을 관리하기 위해 ConcurrentHashMap을 사용
//  private final ConcurrentHashMap<Long, Lock> map = new ConcurrentHashMap<>();
//
//  public <T> T lock(long id, Supplier<T> task) {
//    // ReentrantLock - sync = fair ? new FairSync() : new NonfairSync();
//    // 공정성 옵션으로 Queue가 생성된다. ( 먼저 요청한 순서대로 동작 한다 )
//    Lock lock = map.computeIfAbsent(id, k -> new ReentrantLock(true));
//
//    lock.lock();
//    try {
//      return task.get();
//    } finally {
//      lock.unlock();
//    }
//  }
//}
