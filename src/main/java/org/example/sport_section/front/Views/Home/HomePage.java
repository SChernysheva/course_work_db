package org.example.sport_section.front.Views.Home;

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
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.Court;
import org.example.sport_section.Utils.SecurityUtils;
import org.example.sport_section.front.Views.Authorize.StartPage;
import org.example.sport_section.front.Views.CourtPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Route("home")
public class HomePage extends HorizontalLayout {
    private final String imageURL = "https://a-static.besthdwallpaper.com/tennis-ball-standing-on-clay-tennis-court-on-a-sunlit-day-wallpaper-5120x3200-88002_10.jpg";
    private FlexLayout cardLayout = new FlexLayout();
    private final Div loadingSpinner = createLoadingSpinner();
    private final WebClient webClient;

    @Autowired
    public HomePage(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api").build();
        setSizeFull();
        setSpacing(false);
        add(loadingSpinner);
        webClient.get().uri("/courts").retrieve().bodyToMono(Court[].class).subscribe(courts -> {
            List<Court> courtList = Arrays.asList(courts);
            getUI().ifPresent(ui -> ui.access(() -> {
                remove(loadingSpinner);
                loadContent(courtList);
            }));
        });
        UI.getCurrent().setPollInterval(500);
    }

    private Button getExitButton() {
        Button exitButton = new Button("Выйти");

        exitButton.getStyle().set("background-color", "orange");
        exitButton.getStyle().set("color", "white");
        exitButton.getStyle().set("font-size", "12px");
        exitButton.getStyle().set("border", "none");
        exitButton.getStyle().set("padding", "5px 10px");

        exitButton.addClickListener(event -> {
            exit();
        });
        return exitButton;
    }
    private VerticalLayout createSidebarViewUser() {
        VerticalLayout sidebar = new VerticalLayout();

        // Получение информации о текущем пользователе в сессии
        String userEmail = (String) VaadinSession.getCurrent().getAttribute("email");

        // Создание контейнера с информацией о пользователе
        Div userContainer = new Div();
        userContainer.getStyle().set("background-color", "lightgray");
        userContainer.getStyle().set("padding", "5px");

        // Добавление текста с информацией о пользователе
        Span userText = new Span("Пользователь: " + userEmail);
        userContainer.add(userText);

        // Добавление контейнера с информацией о пользователе на боковую панель
        sidebar.add(userContainer);
        sidebar.setAlignItems(Alignment.CENTER); // Центрирование по вертикали
        sidebar.setWidth("200px"); // Задание ширины боковой панели

        return sidebar;
    }
    private void exit() {
        UI.getCurrent().navigate(StartPage.class);
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
        sidebar.add(createSidebarViewUser());
        sidebar.add(getExitButton());
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
            Div card = createCourtCard(court);
            cardLayout.add(card);
        }
    }

    public void goToCourtInfo(Court court) {
        VaadinSession.getCurrent().setAttribute("court", court);
        UI.getCurrent().navigate(CourtPageInfo.class);
    }

    private Div createCourtCard(Court court) {
        Div div = new Div();
        Button card = new Button(court.getNameCourt());
        card.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");
        card.setWidth("150px");
        card.setHeight("150px");
        card.addClickListener(event -> goToCourtInfo(court));
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
        Button bookingButton = new Button("Мои бронирования");
        trainersButton.addClickListener(event ->
                UI.getCurrent().navigate(HomePage.class));
        trainersButton.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");
        bookingButton.addClickListener(event ->
                UI.getCurrent().navigate(HomePage.class));
        bookingButton.getStyle().set("background-color", "#FFFFFF")
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
        sidebar.add(bookingButton);

        return sidebar;
    }
}
