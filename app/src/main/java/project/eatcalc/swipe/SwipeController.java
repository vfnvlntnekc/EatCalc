package project.eatcalc.swipe;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import project.eatcalc.R;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;

enum ButtonState {
    GONE,
    VISIBLE
}

public class SwipeController extends ItemTouchHelper.Callback {

    private boolean swipeBack = false;
    private ButtonState buttonState = ButtonState.GONE;
    private RecyclerView.ViewHolder currentItemViewHolder = null;
    private RectF buttonRightInstance = null;
    private RectF buttonLeftInstance = null;
    private final SwipeControllerActions buttonsActions;
    private static final float buttonsWidth = 300;

    public SwipeController(SwipeControllerActions buttonsActions) {
        this.buttonsActions = buttonsActions;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if(swipeBack) {
            swipeBack = buttonState != ButtonState.GONE;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        if(actionState == ACTION_STATE_SWIPE) {
            if (buttonState != ButtonState.GONE) {
                dX = Math.min(dX, -buttonsWidth);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if(buttonState == ButtonState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        currentItemViewHolder = viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(@NonNull Canvas c,
                                  @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  float dX, float dY,
                                  int actionState, boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL ||
                        event.getAction() == MotionEvent.ACTION_UP;
            if(swipeBack) {
                if(dX < -buttonsWidth) {
                    buttonState = ButtonState.VISIBLE;
                }
                if(buttonState != ButtonState.GONE) {
                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    viewHolder.itemView.setClickable(false);
                }
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchDownListener(final Canvas c,
                                      final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY,
                                      final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(final Canvas c,
                                    final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY,
                                    final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                recyclerView.setOnTouchListener((rV, rEvent) -> false);
                viewHolder.itemView.setClickable(true);
                swipeBack = false;
                if (buttonLeftInstance != null && buttonLeftInstance.contains(event.getX(), event.getY())) {
                    if (buttonState == ButtonState.VISIBLE) {
                        buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
                    }
                }
                if (buttonRightInstance != null && buttonRightInstance.contains(event.getX(), event.getY())) {
                    if (buttonState == ButtonState.VISIBLE) {
                        buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
                    }
                }
                buttonState = ButtonState.GONE;
                currentItemViewHolder = null;
            }
            return false;
        });
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        final float buttonWidthWithoutPadding = buttonsWidth / 2;
        final float paddingTop = 5;
        final float paddingEnd = 5;
        final float paddingBottom = 5;

        float corners = 0;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        RectF leftButton = new RectF(
                itemView.getRight() - buttonWidthWithoutPadding*2,
                itemView.getTop() + paddingTop,
                itemView.getRight() - buttonWidthWithoutPadding,
                itemView.getBottom() - paddingBottom
        );
        p.setColor(Color.WHITE);
        c.drawRoundRect(leftButton, corners, corners, p);
        drawText(viewHolder.itemView.getContext().getString(R.string.to_edit), c, leftButton, p);

        RectF rightButton = new RectF(
                itemView.getRight() - buttonWidthWithoutPadding,
                itemView.getTop() + paddingTop,
                itemView.getRight() - paddingEnd,
                itemView.getBottom() - paddingBottom
        );
        p.setColor(Color.WHITE);
        c.drawRoundRect(rightButton, corners, corners, p);
        drawText(viewHolder.itemView.getContext().getString(R.string.delete), c, rightButton, p);

        buttonLeftInstance = null;
        buttonRightInstance = null;
        if (buttonState == ButtonState.VISIBLE) {
            buttonLeftInstance = leftButton;
            buttonRightInstance = rightButton;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 20;
        p.setColor(Color.BLACK);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }

    public void onDraw(Canvas c) {
        if (currentItemViewHolder != null) {
            drawButtons(c, currentItemViewHolder);
        }
    }
}
