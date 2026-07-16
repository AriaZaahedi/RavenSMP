package ir.ariwuh.plugin.ravensmp.model.account;

import ir.ariwuh.plugin.ravensmp.api.account.RavenAccountSettings;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@NoArgsConstructor
@Data
public final class SMPAccountSettings implements RavenAccountSettings {

    public static @NotNull SMPAccountSettings defaultSettings() {
        val accountSettings = new SMPAccountSettings();
        accountSettings.teamInvitesDisabled(false);

        return accountSettings;
    }

    private boolean teamInvitesDisabled;

}