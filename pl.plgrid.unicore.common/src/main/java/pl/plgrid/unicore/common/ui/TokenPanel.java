package pl.plgrid.unicore.common.ui;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.ui.Styles;

import java.util.List;

public class TokenPanel extends HorizontalLayout {
    private final List<String> tokens = Lists.newArrayList();

    public TokenPanel() {
        initializeComponents();
    }

    public TokenPanel(List<String> initTokens) {
        tokens.addAll(initTokens);
        initializeComponents();
    }


    private void initializeComponents() {
        for (String token : tokens) {
            NativeButton tokenButton = new NativeButton();
            configureTokenButton(token, tokenButton);

            addComponent(tokenButton);
            setExpandRatio(tokenButton, 1.f);
        }
        setSizeFull();
        setMargin(true);
        setCaption("Tokens");
        addStyleName(Styles.PADDING_All_10);
//        setSpacing(true);
    }

    private void configureTokenButton(String token, Button tokenButton) {
        final String tokenCaptionSuffix = " ×";
        tokenButton.setCaption(token + tokenCaptionSuffix);
        tokenButton.setDescription("Click to remove");
        tokenButton.setStyleName(Reindeer.BUTTON_LINK);

        tokenButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String caption = event.getButton().getCaption();
                String token = caption
                        .substring(0,
                                caption.length() - tokenCaptionSuffix.length());

                removeComponent(event.getButton());
                tokens.remove(token);
            }
        });
    }
}
