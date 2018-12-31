package net.runningcode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.runningcode.R;
import net.runningcode.utils.OnAntiShakeClickListener;

/**
 * Author： chenchongyu
 * Date: 2018/12/29
 * Description:
 */
public class SearchView extends ConstraintLayout {
    private EditText mEditText;
    private Group mGroup;
    private ImageView mBtn, mClear;
    private OnTextChangedListener onTextChangedListener;
    private OnQueryClickListener onSearchClickListener;
    private String hint;
    private int inputType;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchView);
        hint = typedArray.getString(R.styleable.SearchView_hint);
        inputType = typedArray.getInt(R.styleable.SearchView_input_type, 1);
        typedArray.recycle();
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.searview, this);
        mBtn = view.findViewById(R.id.search_btn);
        mClear = view.findViewById(R.id.search_clear);
        mGroup = view.findViewById(R.id.search_clear_group);
        mEditText = view.findViewById(R.id.search_content);
        mEditText.setHint(hint);
        mEditText.setInputType(inputType);
        mClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mGroup.setVisibility(VISIBLE);
                } else {
                    mGroup.setVisibility(GONE);
                }
                if (onTextChangedListener != null) {
                    onTextChangedListener.onTextChanged(s.toString());
                }

            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (onSearchClickListener != null) {
                        onSearchClickListener.onClick(mEditText.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });

        mBtn.setOnClickListener(new OnAntiShakeClickListener() {
            @Override
            public void onAntiShakeClick(View v) {
                if (onSearchClickListener != null) {
                    onSearchClickListener.onClick(mEditText.getText().toString());
                }
            }
        });

    }

    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
    }

    public void setOnSearchClickListener(OnQueryClickListener onSearchClickListener) {
        this.onSearchClickListener = onSearchClickListener;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void hideQueryBtn() {
        mBtn.setVisibility(GONE);
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public interface OnTextChangedListener {
        void onTextChanged(String ss);
    }

    public interface OnQueryClickListener {
        void onClick(String key);

    }

}
