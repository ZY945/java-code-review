package DesignPattern.all;

interface Button {
    void render();
}

interface Window {
    void show();
}

class WindowsButton implements Button {
    public void render() {
        System.out.println("Rendering Windows Button");
    }
}

class WindowsWindow implements Window {
    public void show() {
        System.out.println("Showing Windows Window");
    }
}

interface UIFactory {
    Button createButton();
    Window createWindow();
}

class WindowsFactory implements UIFactory {
    public Button createButton() {
        return new WindowsButton();
    }
    public Window createWindow() {
        return new WindowsWindow();
    }
}

public class AbstractFactory {
    public static void main(String[] args) {
        UIFactory factory = new WindowsFactory();
        Button button = factory.createButton();
        Window window = factory.createWindow();
        button.render();
        window.show();
    }
}