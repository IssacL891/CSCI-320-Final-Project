package com.CSCI32006.CLI;

import org.jetbrains.annotations.NotNull;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.style.TemplateExecutor;

public class Helper {
    public static @NotNull String getContextValue(boolean mask, String name, String defaultValue,
                                             Terminal terminal, ResourceLoader loader, TemplateExecutor executor) {
        StringInput component = new StringInput(terminal, name, defaultValue);
        component.setResourceLoader(loader);
        component.setTemplateExecutor(executor);
        if(mask) {
            component.setMaskCharacter('*');
        }

        return component.run(StringInput.StringInputContext.empty()).getResultValue();
    }


}
