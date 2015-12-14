package list;

import target.Target;

/**
 * Created by novik on 14.12.15.
 */
public interface TargetClickHandler extends SelectedListener<Target> {
    public void onLongClick(Target target);
}