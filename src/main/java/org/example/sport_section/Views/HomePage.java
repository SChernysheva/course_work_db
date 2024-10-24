package org.example.sport_section.Views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.Court;
import org.example.sport_section.Models.User;
import org.example.sport_section.Utils.SecurityUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Route("home")
public class HomePage extends HorizontalLayout {
    private final String imageURL = "https://a-static.besthdwallpaper.com/tennis-ball-standing-on-clay-tennis-court-on-a-sunlit-day-wallpaper-5120x3200-88002_10.jpg";
    private FlexLayout cardLayout = new FlexLayout();
    private final Div loadingSpinner = createLoadingSpinner();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public HomePage() {
        setSizeFull();
        setSpacing(false);
        add(loadingSpinner);
        CompletableFuture.supplyAsync(this::fetchCourts, executor)
                .thenAcceptAsync(courts -> {
                    getUI().ifPresent(ui -> ui.access(() ->  {
                        remove(loadingSpinner);
                        loadContent(courts);
                        User user = SecurityUtils.getCurrentUser();
                        System.out.println(user);
                    }));
                });


        UI.getCurrent().setPollInterval(500);
    }

    private void loadContent(List<Court> courts) {
        // Создаем и добавляем боковую панель
        VerticalLayout sidebar = createSidebarView();
        Div imageContainer = addImageContainer();
        Div overlay = getOverlay();

        cardLayout.setWidthFull();

        cardLayout.getStyle().set("flex-wrap", "wrap").set("gap", "10px");
        cardLayout.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        cardLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Выравнивание по вертикали
        cardLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Выравнивание по горизонтали
        cardLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Центруем по вертикали
        cardLayout.setAlignItems(Alignment.CENTER); // Центруем по горизонтали
        overlay.add(cardLayout);
        imageContainer.add(overlay);
        add(sidebar);
        add(imageContainer);
        // Обновление списков
        addCourtsToView(courts);
    }

    public Div getOverlay() {
        // Добавляем заголовок и карточки на изображение
        Div overlay = new Div();
        overlay.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background-color", "rgba(255, 255, 255, 0.7)"); // Полупрозрачный фон

        Text title = new Text("Бронирование кортов");
        Div titleContainer = new Div(title);
        titleContainer.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("text-decoration", "underline")
                .set("font-family", "Arial, sans-serif")
                .set("margin-bottom", "20px");

        overlay.add(titleContainer);
        return overlay;
    }
    public Div addImageContainer() {
        Div imageContainer = new Div();
        imageContainer.getStyle()
                .set("position", "relative")   // Позволяет позиционирование вложенных элементов
                .set("background-color", "#f0f0f0")
                .set("border-radius", "10px")
                .set("overflow", "hidden");    // Обрезает выступающие части
        imageContainer.setSizeFull();

        Image image = new Image(imageURL, "Описание изображения");
        image.setWidth("100%");
        image.setHeight("100%");
        image.getStyle().set("object-fit", "cover");  // Контролирует размер изображения
        imageContainer.add(image);
        return imageContainer;
    }
    private void addCourtsToView(List<Court> courts) {
        for (Court court : courts) {
            Div card = createCourtCard(court.getNameCourt());
            cardLayout.add(card);
        }
    }

    private Div createCourtCard(String courtName) {
        Div div = new Div();
        Button card = new Button(courtName);
        card.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");
        card.setWidth("150px");
        card.setHeight("150px");
        div.add(card);
        return div;
    }

    private Div createLoadingSpinner() {
        Div spinner = new Div();
        spinner.add(new Span("Loading..."));
        spinner.getStyle().set("display", "flex");
        spinner.getStyle().set("width", "100%");
        spinner.getStyle().set("height", "100%");

        spinner.getStyle().set("align-items", "center");
        spinner.getStyle().set("justify-content", "center");
        spinner.getStyle().set("font-size", "24px");
        spinner.getStyle().set("font-weight", "bold");
        return spinner;
    }

    private List<Court> fetchCourts() {
        RestTemplate restTemplate = new RestTemplate();
        Court[] courtsArray = restTemplate.getForObject("http://localhost:8080/api/courts", Court[].class);
        return Arrays.asList(courtsArray);
    }

    private VerticalLayout createSidebarView() {
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setWidth("250px");

        // Изменяем цвет фона на чуть более темный и закругляем углы
        sidebar.getStyle()
                .set("background-color", "#F2F3F4")
                .set("border-radius", "10px")        // Закругляем углы
                .set("padding", "15px");             // Добавляем внутренние отступы для эстетики

        // Добавляем кнопки для навигации
        Button trainersButton = new Button("Наши тренеры");
        trainersButton.addClickListener(event ->
                UI.getCurrent().navigate(HomePage.class));
        trainersButton.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");

        Button scheduleButton = new Button("Расписание занятий");
        scheduleButton.addClickListener(event ->
                UI.getCurrent().navigate(HomePage.class));
        scheduleButton.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");

        // Добавляем кнопки в сайдбар
        sidebar.add(trainersButton);
        sidebar.add(scheduleButton);

        return sidebar;
    }
}
