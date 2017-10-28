package org.openbase.bco.bcozy.permissions;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import org.openbase.jul.exception.CouldNotPerformException;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author vdasilva
 */
public class OwnerPermissionsController {

    private static final Logger LOG = Logger.getLogger(OwnerPermissionsController.class.getName());
    @FXML
    private CheckBox read;
    @FXML
    private CheckBox access;
    @FXML
    private CheckBox write;
    @FXML
    private ChoiceBox<OwnerViewModel> owner;

    protected PermissionsService permissionsService = PermissionsServiceImpl.permissionsService;


    protected void updateOwnerContent(String selectedUnitId) throws CouldNotPerformException, InterruptedException {
        final List<OwnerViewModel> ownerModels = permissionsService.getOwners(selectedUnitId);

        owner.getItems().setAll(ownerModels);
        owner.getItems().add(0, OwnerViewModel.NULL_OBJECT);

        final OwnerViewModel currentOwner = ownerModels.stream().filter(OwnerViewModel::isCurrentOwner).findAny().orElse(null);
        owner.getSelectionModel().select(currentOwner);

        if (currentOwner != null) {
            read.setSelected(currentOwner.isRead());
            access.setSelected(currentOwner.isAccess());
            write.setSelected(currentOwner.isWrite());
        } else {
            read.setSelected(false);
            access.setSelected(false);
            write.setSelected(false);
        }
    }

    public OwnerViewModel getSelectedOwner() {
        if (owner.getValue() != null) {
            OwnerViewModel ownerViewModel = owner.getValue();
            ownerViewModel.setAccess(access.isSelected());
            ownerViewModel.setRead(read.isSelected());
            ownerViewModel.setWrite(write.isSelected());
            return ownerViewModel;
        }

        return OwnerViewModel.NULL_OBJECT;
    }
}
