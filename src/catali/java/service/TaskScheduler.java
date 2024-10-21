package catali.java.service;

import java.util.concurrent.*;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class TaskScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    private final Map<String, ScheduledFuture<?>> taskMap = new HashMap<>();

    /**
     * Thêm một Runnable để chạy lặp lại sau một khoảng thời gian nhất định.
     * @param runnable Nhiệm vụ cần chạy.
     * @param initialDelay Thời gian chờ trước lần chạy đầu tiên (millisecond).
     * @param period Thời gian giữa các lần chạy tiếp theo (millisecond).
     * @return id của nhiệm vụ vừa được thêm.
     */
    public String addTask(Runnable runnable, long initialDelay, long period) {
        String taskId = UUID.randomUUID().toString();
        ScheduledFuture<?> scheduledTask = scheduler.scheduleAtFixedRate(runnable, initialDelay, period, TimeUnit.MILLISECONDS);
        taskMap.put(taskId, scheduledTask);
        return taskId;
    }

    /**
     * Hủy một nhiệm vụ theo id.
     * @param taskId id của nhiệm vụ cần xóa.
     * @return true nếu nhiệm vụ đã được hủy thành công, false nếu không tìm thấy id.
     */
    public boolean removeTask(String taskId) {
        ScheduledFuture<?> scheduledTask = taskMap.get(taskId);

        if (scheduledTask != null) {
            scheduledTask.cancel(true);  
            taskMap.remove(taskId);     
            return true;
        } else {
            System.out.println("Task with ID: " + taskId + " not found.");
            return false;
        }
    }

    /**
     * Dừng tất cả các nhiệm vụ và tắt bộ lịch.
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TaskScheduler taskScheduler = new TaskScheduler();

        taskScheduler.addTask(() -> System.out.println("Task 1 running"), 0, 1000);
        String taskId2 = taskScheduler.addTask(() -> System.out.println("Task 2 running"), 0, 2000);
        Thread.sleep(10000);
        taskScheduler.removeTask(taskId2);
        Thread.sleep(5000);
        taskScheduler.shutdown();
    }
}
