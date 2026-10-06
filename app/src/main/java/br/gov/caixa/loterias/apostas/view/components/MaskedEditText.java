package br.gov.caixa.loterias.apostas.view.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.R;

public class MaskedEditText extends AppCompatEditText {

    private static final char MASK_PLACEHOLDER = '#';

    @Nullable
    private String mask;

    private boolean isFormatting;

    @ColorInt
    private int maskColor;

    public MaskedEditText(@NonNull Context context) {
        super(context);
        init(null);
    }

    public MaskedEditText(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
        init(attrs);
    }

    public MaskedEditText(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        maskColor = ContextCompat.getColor(
                getContext(),
                R.color.cinza_item_desabilitado
        );

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(
                    attrs,
                    R.styleable.MaskedEditText
            );

            try {
                mask = typedArray.getString(
                        R.styleable.MaskedEditText_mask
                );

                maskColor = typedArray.getColor(
                        R.styleable.MaskedEditText_maskColor,
                        maskColor
                );
            } finally {
                typedArray.recycle();
            }
        }

        /*
         * Evita conflito entre os spans internos do EmojiCompat e os spans
         * utilizados para colorir a máscara.
         */
        setEmojiCompatEnabled(false);

        configureMaxLength();
        addTextChangedListener(maskWatcher);
    }

    private final TextWatcher maskWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(
                CharSequence value,
                int start,
                int count,
                int after
        ) {
            // Não utilizado.
        }

        @Override
        public void onTextChanged(
                CharSequence value,
                int start,
                int before,
                int count
        ) {
            // Não utilizado.
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (isFormatting || mask == null || mask.isEmpty()) {
                return;
            }

            String rawValue = getRawValue(editable.toString());
            SpannableString formattedValue = createFormattedText(rawValue);

            isFormatting = true;
            removeTextChangedListener(this);

            try {
                /*
                 * Não utiliza editable.replace(), pois ele pode manter spans
                 * antigos do EmojiCompat apontando para posições inválidas.
                 */
                setText(formattedValue, BufferType.SPANNABLE);

                Editable currentText = getText();

                if (currentText != null) {
                    int safeSelection = Math.min(
                            formattedValue.length(),
                            currentText.length()
                    );

                    setSelection(Math.max(0, safeSelection));
                }
            } finally {
                addTextChangedListener(this);
                isFormatting = false;
            }
        }
    };

    @NonNull
    private SpannableString createFormattedText(@NonNull String rawValue) {
        if (mask == null || mask.isEmpty() || rawValue.isEmpty()) {
            return new SpannableString(rawValue);
        }

        StringBuilder formatted = new StringBuilder();
        boolean[] maskCharacterPositions = new boolean[mask.length()];

        int rawIndex = 0;

        for (int maskIndex = 0;
             maskIndex < mask.length() && rawIndex < rawValue.length();
             maskIndex++) {

            char maskCharacter = mask.charAt(maskIndex);

            if (maskCharacter == MASK_PLACEHOLDER) {
                formatted.append(rawValue.charAt(rawIndex));
                rawIndex++;
            } else {
                int position = formatted.length();

                formatted.append(maskCharacter);

                if (position < maskCharacterPositions.length) {
                    maskCharacterPositions[position] = true;
                }
            }
        }

        SpannableString spannable = new SpannableString(
                formatted.toString()
        );

        for (int index = 0; index < spannable.length(); index++) {
            if (index < maskCharacterPositions.length
                    && maskCharacterPositions[index]) {

                spannable.setSpan(
                        new ForegroundColorSpan(maskColor),
                        index,
                        index + 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }

        return spannable;
    }

    @NonNull
    private String getRawValue(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        StringBuilder rawValue = new StringBuilder();

        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);

            if (Character.isLetterOrDigit(character)) {
                rawValue.append(character);
            }
        }

        return rawValue.toString();
    }

    private void configureMaxLength() {
        if (mask == null || mask.isEmpty()) {
            return;
        }

        setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(mask.length())
        });
    }

    public void setMask(@Nullable String mask) {
        this.mask = mask;
        configureMaxLength();
        formatCurrentText();
    }

    @Nullable
    public String getMask() {
        return mask;
    }

    public void setMaskColor(@ColorInt int color) {
        maskColor = color;
        formatCurrentText();
    }

    @NonNull
    public String getRawText() {
        Editable editable = getText();

        return editable == null
                ? ""
                : getRawValue(editable.toString());
    }

    public boolean isComplete() {
        if (mask == null || mask.isEmpty()) {
            return !getRawText().isEmpty();
        }

        int expectedLength = 0;

        for (int index = 0; index < mask.length(); index++) {
            if (mask.charAt(index) == MASK_PLACEHOLDER) {
                expectedLength++;
            }
        }

        return getRawText().length() == expectedLength;
    }

    private void formatCurrentText() {
        Editable editable = getText();

        if (editable == null) {
            return;
        }

        String rawValue = getRawValue(editable.toString());
        SpannableString formattedValue = createFormattedText(rawValue);

        isFormatting = true;
        removeTextChangedListener(maskWatcher);

        try {
            setText(formattedValue, BufferType.SPANNABLE);

            Editable currentText = getText();

            if (currentText != null) {
                setSelection(
                        Math.min(formattedValue.length(), currentText.length())
                );
            }
        } finally {
            addTextChangedListener(maskWatcher);
            isFormatting = false;
        }
    }
}