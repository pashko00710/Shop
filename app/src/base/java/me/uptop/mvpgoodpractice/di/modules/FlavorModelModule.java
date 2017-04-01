package me.uptop.mvpgoodpractice.di.modules;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import dagger.Module;
import dagger.Provides;
import me.uptop.mvpgoodpractice.utils.ConstantManager;
import me.uptop.mvpgoodpractice.utils.MvpAuthApplication;

@Module
public class FlavorModelModule {
    @Provides
    JobManager provideJobManager() {
        Configuration configuration = new Configuration.Builder(MvpAuthApplication.getContext())
                .minConsumerCount(ConstantManager.MIN_CONSUMER_COUNT) //минимальное кол-во потоков для решения задачи
                .maxConsumerCount(ConstantManager.MAX_CONSUMER_COUNT) //максимальное кол-во потоков для решения задачи
                .loadFactor(ConstantManager.LOAD_FACTOR) // кол-во задач на один поток
                .consumerKeepAlive(ConstantManager.KEEP_ALIVE) // ожидание 2 минуты на поток
                .build();

        return new JobManager(configuration);
    }
}
