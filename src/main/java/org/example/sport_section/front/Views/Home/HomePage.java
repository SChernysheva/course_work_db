package org.example.sport_section.front.Views.Home;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.Courts.Court;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.Services.ImageService.ImageService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.ImageHelper;
import org.example.sport_section.Utils.Security.SecurityUtils;
//import org.example.sport_section.front.Views.Authorize.StartPage;
import org.example.sport_section.front.Views.UsersViews.UpdateUserInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.example.sport_section.front.Views.Sidebar.createSidebarView;


@Route("")
public class HomePage extends HorizontalLayout {
    private final CourtService courtService;
    private FlexLayout cardLayout = new FlexLayout();
    private final Div loadingSpinner = createLoadingSpinner();
    private final ImageService imageService;
    private final UserService userService;

    @Autowired
    public HomePage(CourtService courtService, ImageService imageService, UserService userService) {
        this.userService = userService;
        this.imageService = imageService;
        this.courtService = courtService;
        setSizeFull();
        setSpacing(false);
        add(loadingSpinner);
        String userEmail = SecurityUtils.getCurrentUserEmail();
        Optional<User> userOpt = userService.getUserAsync(userEmail).join();
        if (!userOpt.isPresent()) {
            UI.getCurrent().access(() -> {
                Notification.show("Что-то пошло не так, попробуйте авторизоваться еще раз", 5000, Notification.Position.MIDDLE);
                SecurityUtils.deleteAuth();
            });
        }
        User user = userOpt.get();
        Div imageContainer = addImageContainer();
        List<Court> courts = courtService.getCourtsAsync().join();
        UI.getCurrent().access(() -> {
            remove(loadingSpinner);
            loadContent(courts, userEmail, user, imageContainer);
        });
    }
    private VerticalLayout createSidebarViewUser(String userEmail, int userId) {
        VerticalLayout sidebar = new VerticalLayout();
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

    private void loadContent(List<Court> courts, String userEmail, User user, Div imageContainer) {
        // Создаем и добавляем боковую панель
        VerticalLayout sidebar = createSidebarView(HomePage.class, UI.getCurrent(), user);
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
        Optional<org.example.sport_section.Models.Images.Image> imageValue = imageService.getImageByPage("home").join();
        Image image = null;
        if (imageValue.isPresent()) {
            image = ImageHelper.createImageFromByteArray(imageValue.get().getImage_data(), "Описание");
            image.setWidth("100%");
            image.setHeight("100%");
            image.getStyle().set("object-fit", "cover");  // Контролирует размер изображения
            imageContainer.add(image);
        }
        return imageContainer;
    }
    private void addCourtsToView(List<Court> courts) {
        for (Court court : courts) {
            Div card = createCourtCard(court);
            cardLayout.add(card);
        }
    }

    public void goToCourtInfo(Court court) {
        System.out.println(court.getId());
        UI.getCurrent().navigate("/courts/info/" + court.getId());
    }

    private Div createCourtCard(Court court) {
        Div div = new Div();
        Button card = new Button(court.getCourtName());
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



}
