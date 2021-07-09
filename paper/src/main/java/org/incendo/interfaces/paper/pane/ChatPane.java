package org.incendo.interfaces.paper.pane;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.element.Element;
import org.incendo.interfaces.core.pane.Pane;
import org.incendo.interfaces.paper.element.ChatLineElement;
import org.incendo.interfaces.paper.element.ClickableTextElement;
import org.incendo.interfaces.paper.element.TextElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A pane using the Minecraft chat window..
 */
public class ChatPane implements Pane {

    private final @NonNull List<ChatLineElement> elements;
    private final @NonNull Map<UUID, ClickHandler<ChatPane, InventoryClickEvent, ? extends ClickContext<ChatPane,
            InventoryClickEvent>>> handlerMap;

    /**
     * Constructs {@code ChatPane}.
     *
     * @param elements the text elements of this pane
     */
    public ChatPane(final @NonNull List<ChatLineElement> elements) {
        this.elements = elements;
        this.handlerMap = new HashMap<>();

        for (final @NonNull ChatLineElement element : this.elements) {
            for (final @NonNull TextElement textElement : element.textElements()) {
                if (textElement instanceof ClickableTextElement) {
                    final @NonNull ClickableTextElement clickable = (ClickableTextElement) textElement;
                    this.handlerMap.put(clickable.uuid(), clickable.clickHandler());
                }
            }
        }
    }

    /**
     * Returns the elements of the chest as a 2d array.
     *
     * @return the elements
     */
    public @NonNull List<ChatLineElement> textElements() {
        return this.elements;
    }

    /**
     * Returns the elements, where the first 9 elements are row 1, the second 9 elements are row 2, etc...
     *
     * @return the elements collection
     */
    @Override
    public @NonNull Collection<Element> elements() {
        final @NonNull List<Element> tempElements = new ArrayList<>(this.elements);

        return tempElements;
    }

    /**
     * Puts a text element at the given line.
     * <p>
     * This method returns an updated instance of {@code ChatPane} with the new element.
     *
     * @param element the element
     * @param index   the line index
     * @return a new {@code ChatPane}
     */
    public @NonNull ChatPane element(final @NonNull ChatLineElement element, final int index) {
        final @NonNull List<ChatLineElement> newElements = new ArrayList<>(this.elements);

        newElements.set(index, element);

        return new ChatPane(newElements);
    }

    /**
     * Puts a text element at the the end of the pane.
     * <p>
     * This method returns an updated instance of {@code ChatPane} with the new element.
     *
     * @param element the element
     * @return a new {@code ChatPane}
     */
    public @NonNull ChatPane element(final @NonNull ChatLineElement element) {
        final @NonNull List<ChatLineElement> newElements = new ArrayList<>(this.elements);

        newElements.add(element);

        return new ChatPane(newElements);
    }

    /**
     * Returns the chat element at the given line in dex.
     *
     * @param index the line index
     * @return the element
     */
    public @NonNull ChatLineElement element(final int index) {
        return this.elements.get(index);
    }

}