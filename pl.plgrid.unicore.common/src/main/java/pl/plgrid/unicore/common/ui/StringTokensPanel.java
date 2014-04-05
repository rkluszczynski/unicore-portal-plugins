package pl.plgrid.unicore.common.ui;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.ui.Styles;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringTokensPanel extends VerticalLayout {
    private static final Logger logger = Logger.getLogger(StringTokensPanel.class);

    private final Map<String, Button> tokens = Maps.newHashMap();

    public StringTokensPanel() {
        initializeComponents(Lists.<String>newArrayList());
    }

    public StringTokensPanel(List<String> initTokens) {
        initializeComponents(initTokens);
    }


    private void initializeComponents(List<String> initTokens) {
        for (String token : initTokens) {
            createTokenWithButton(token);
        }
        setSizeFull();
        setMargin(true);
        addStyleName(Styles.PADDING_All_10);
//        setSpacing(true);
    }

    public void putToken(String key, String newToken) {
        Set<Map.Entry<String, Button>> entries = tokens.entrySet();
        for (Map.Entry<String, Button> entry : entries) {
            String entryKey = entry.getKey();
            if (entryKey.startsWith(key)) {
                removeComponent(entry.getValue());
            }
        }
        createTokenWithButton(newToken);
    }

    private void createTokenWithButton(String token) {
        NativeButton tokenButton = new NativeButton();
        configureTokenButton(token, tokenButton);
        tokens.put(token, tokenButton);

        addComponent(tokenButton);
        setExpandRatio(tokenButton, 1.f);
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
