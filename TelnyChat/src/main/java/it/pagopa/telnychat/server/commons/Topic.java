package it.pagopa.telnychat.server.commons;

import it.pagopa.telnychat.server.impl.TelnyClientHandler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Objects;

/**
 * A Topic (an Observable), identified by Name, where Clients can sign up.
 * Messages sended on a Topic are broadcasted to all Clients signed on.
 */
public class Topic
{
    private String name;                        // The name of this topic
    private String message;                     // Last message broadcasted to clients (observers) listening on this Topic
    private PropertyChangeSupport observers;    // The clients (observers) listening on this Topic

    public Topic(String name) {
        this.name = name;
        observers = new PropertyChangeSupport(this);
    }

    public void addObserver(PropertyChangeListener observer) {
        observers.addPropertyChangeListener(observer);
    }

    public void removeObserver(PropertyChangeListener observer) {
        observers.removePropertyChangeListener(observer);
    }

    public void sendMessageToObservers(String author,String message) {
        PropertyChangeListener[] observersList = observers.getPropertyChangeListeners();
        Arrays.stream(observersList)
              .filter(observer -> !Objects.equals(((TelnyClientHandler) observer).whois(),author))
              .forEach(observer ->
                {
                    PropertyChangeEvent evt = new PropertyChangeEvent(this,"message", this.message, message);
                    observer.propertyChange(evt);
                });
        this.message = message;
    }

    public String getName() {
        return name;
    }
}
