package com.osrs.fliptool;

import com.osrs.fliptool.service.GEItem;
import com.osrs.fliptool.service.GEItemService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Route("")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends VerticalLayout {
    private static final String APP_TITLE = "OSRS Flipping Tool";
    private static final Map<ValueProvider<GEItem, ?>, String> COLUMN_HEADINGS;
    static {
        COLUMN_HEADINGS = new LinkedHashMap<>();
        COLUMN_HEADINGS.put(GEItem::getName, "Item");
        COLUMN_HEADINGS.put(GEItem::getId, "ID");
        COLUMN_HEADINGS.put(GEItem::getBuyAvg, "Avg Buy Price (GP)");
        COLUMN_HEADINGS.put(GEItem::getSellAvg, "Avg Sell Price (GP)");
        COLUMN_HEADINGS.put(GEItem::getBuyQuantity, "Buy Quantity");
        COLUMN_HEADINGS.put(GEItem::getSellQuantity, "Sell Quantity");
        COLUMN_HEADINGS.put(GEItem::getProfitGP, "Profit per Item (GP)");
        COLUMN_HEADINGS.put(GEItem::getProfitPercent, "Profit per Item (%)");
        COLUMN_HEADINGS.put(GEItem::getPotentialProfit, "Potential Profit (GP)");
    }
    private static final String OSBUDDY_API_URL = "https://rsbuddy.com/exchange";

    @Autowired
    public MainView(GEItemService geItemService) {
        setupView();

        List<GEItem> items = getItems(geItemService);
        Grid<GEItem> itemGrid = setupItemGrid(items);

        VerticalLayout layout = setupLayout();
        layout.add(new H1(APP_TITLE));
        layout.add(itemGrid);

        add(layout);
    }

    private void setupView() {
        setSizeFull();
    }

    private List<GEItem> getItems(GEItemService GEItemService) {
        return GEItemService.generateFlipList(Integer.MAX_VALUE, 0);
    }

    private Grid<GEItem> setupItemGrid(List<GEItem> items) {
        Grid<GEItem> itemGrid = initializeItemGrid(items);
        addColumnsToGrid(itemGrid);
        addShiftClickOpenItemEvent(itemGrid);
        itemGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.MATERIAL_COLUMN_DIVIDERS);
        return itemGrid;
    }

    private Grid<GEItem> initializeItemGrid(List<GEItem> items) {
        Grid<GEItem> itemGrid = new Grid<>();
        itemGrid.setItems(items);
        itemGrid.setColumnReorderingAllowed(true);
        return itemGrid;
    }

    private void addColumnsToGrid(Grid<GEItem> itemGrid) {
        COLUMN_HEADINGS.forEach((getterMethod, columnName) -> addDefaultColumn(itemGrid, getterMethod, columnName));
    }

    private void addShiftClickOpenItemEvent(Grid<GEItem> itemGrid) {
        itemGrid.addItemClickListener(event -> {
            if (event.isShiftKey()) {
                openItem(event.getItem());
            }
        });
    }

    private void openItem(GEItem item) {
        String osBuddyURI = OSBUDDY_API_URL + "?id=" + item.getId();
        UI.getCurrent().getPage().executeJavaScript("window.open(\"" + osBuddyURI + "\", \"_blank\", \"\");");
    }

    private void addDefaultColumn(Grid<GEItem> grid, ValueProvider<GEItem, ?> value, String columnName) {
        grid.addColumn(value).setHeader(columnName).setResizable(true).setSortable(true);
    }

    private VerticalLayout setupLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        return layout;
    }

}
