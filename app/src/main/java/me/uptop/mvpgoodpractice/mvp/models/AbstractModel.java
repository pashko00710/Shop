package me.uptop.mvpgoodpractice.mvp.models;

import com.birbit.android.jobqueue.JobManager;

import javax.inject.Inject;

import me.uptop.mvpgoodpractice.data.managers.DataManager;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.components.DaggerModelComponent;
import me.uptop.mvpgoodpractice.di.components.ModelComponent;
import me.uptop.mvpgoodpractice.di.modules.ModelModule;

public abstract class AbstractModel {
    @Inject
    DataManager mDataManager;
    @Inject
    JobManager mJobManager;

    public AbstractModel() {
        ModelComponent component = DaggerService.getComponent(ModelComponent.class);
        if(component == null) {
            component = createDaggerComponent();
            DaggerService.registerComponent(ModelComponent.class, component);
            //DataManagerComponent
        }
        component.inject(this);
    }

    public AbstractModel(DataManager dataManager, JobManager jobManager) {
        mDataManager = dataManager;
        mJobManager = jobManager;
    }

    private ModelComponent createDaggerComponent() {
        return DaggerModelComponent.builder()
                .modelModule(new ModelModule())
                .build();
    }


}
