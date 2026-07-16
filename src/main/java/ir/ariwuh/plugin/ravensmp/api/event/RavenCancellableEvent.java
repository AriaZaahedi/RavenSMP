package ir.ariwuh.plugin.ravensmp.api.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.Cancellable;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class RavenCancellableEvent extends RavenEvent implements Cancellable {

    private boolean cancelled;

}