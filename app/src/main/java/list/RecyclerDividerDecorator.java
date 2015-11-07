package list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by novik on 07.11.15.
 */
public class RecyclerDividerDecorator extends RecyclerView.ItemDecoration {

    private final Paint paint;

    public RecyclerDividerDecorator(int color) {
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        int position = parent.getChildLayoutPosition(view);

        int itemCount = adapter.getItemCount();
        int bottom = position >= 0 && position < itemCount - 1 ? 1 : 0;

        outRect.set(0, 0, 0, bottom);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        int itemCount = adapter.getItemCount();

        int width = parent.getWidth();

        for (int i = 0; i < itemCount - 1; ++i) {
            View child = parent.getChildAt(i);
            int position = parent.getChildLayoutPosition(child);
            if (position >= 0 && position < itemCount - 1) {
                float y = child.getBottom() + 1;
                c.drawLine(0, y, width, y, paint);
            }
        }
    }
}
