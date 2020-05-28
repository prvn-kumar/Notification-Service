import { Component } from '@angular/core';
import { ClrLoadingState } from '@clr/angular';
import { InboxService } from './inbox.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  isOpen: boolean;
  inboxName: string;
  inboxLocation: string;
  notificationCount: number;
  configKeySaved: boolean;
  inboxToOpen: string;
  title = 'ACI Webhook App';
  inboxOpened: boolean;
  submitBtnState: ClrLoadingState = ClrLoadingState.DEFAULT;
  openBtnState: ClrLoadingState = ClrLoadingState.DEFAULT;
  modalBtnState: ClrLoadingState = ClrLoadingState.DEFAULT;
  key: string;
  error: any;
  refreshing: boolean;
  openModel: boolean;
  modelSetting: string;
  openingInbox: boolean;
  modelTitle: string;
  modelDesc: string;
  constructor(private inboxService: InboxService) {
    this.openModel = false;
  }

  createInbox() {
    this.error = null;
    this.submitBtnState = ClrLoadingState.LOADING;
    this.inboxService.createInbox()
      .subscribe((data: any) => {
        this.inboxName = data.name;
        this.inboxLocation = data.location;
        this.notificationCount = data.notificationCount;
        this.configKeySaved = data.configKeySaved;
        this.submitBtnState = ClrLoadingState.DEFAULT;
        this.inboxOpened = true;
      },
        error => {
          this.error = error;
          this.submitBtnState = ClrLoadingState.DEFAULT;
        });
  }

  openSaveKeyModal() {
    console.log("save modal clicked.. " + this.openModel);
    this.modelSetting = "save";
    this.modelTitle = "Save configuration key";
    this.modelDesc = "Webhook configuration key required for decryption will be saved for the inbox: " + this.inboxName;
    this.openModel = true;
  }

  openDeleteModal() {
    console.log("delete modal clicked.. " + this.openModel);
    this.modelSetting = "delete";
    this.modelTitle = "Delete configuration key";
    this.modelDesc = "Delete config key from this inbox: " + this.inboxName;
    this.openModel = true;
  }

  cancelSetting() {
    this.openModel = false;
  }
  saveSetting() {
    this.error = null;
    this.modalBtnState = ClrLoadingState.LOADING;
    this.inboxService.saveConfigKey(this.inboxName, this.key)
      .subscribe((data: any) => {
        this.modalBtnState = ClrLoadingState.SUCCESS;
        this.inboxService.refreshInbox();
        setTimeout(() => {
          this.openModel = false;
          this.key = null;
        }, 1000);
      },
        error => {
          this.error = error;
          this.modalBtnState = ClrLoadingState.DEFAULT;
          this.openModel = false;
        });
  }
  deleteSetting() {
    this.error = null;
    this.modalBtnState = ClrLoadingState.LOADING;
    this.inboxService.deleteConfigKey(this.inboxName, this.key)
      .subscribe(() => {
        this.modalBtnState = ClrLoadingState.SUCCESS;
        this.inboxService.refreshInbox();
        setTimeout(() => {
          this.openModel = false;
          this.key = null;
        }, 1000);
      },
        error => {
          this.error = error;
          this.modalBtnState = ClrLoadingState.DEFAULT;
          this.openModel = false;
        });
  }

  isOpenClicked() {
    this.error = null;
    this.isOpen = true;
  }

  cancelOpenInbox() {
    this.error = null;
    this.isOpen = false;
  }

  refresh() {
    this.refreshing = true;
    this.refreshInbox();
    this.inboxService.refreshInbox();
  }

  refreshInbox() {
    this.error = null;
    this.inboxService.getInbox(this.inboxName)
      .subscribe((data: any) => {
        this.notificationCount = data.notificationCount;
        this.refreshing = false;
      },
        error => {
          this.error = error;
          this.refreshing = false;
        });
  }

  openInbox() {
    if (!this.inboxToOpen) {
      this.error = "Inbox name is required to open!";
    }
    else {
      this.error = null;
      this.openingInbox = true;
      this.openBtnState = ClrLoadingState.LOADING;
      this.inboxService.getInbox(this.inboxToOpen)
        .subscribe((data: any) => {
          this.inboxName = data.name;
          this.inboxLocation = data.location;
          this.notificationCount = data.notificationCount;
          this.configKeySaved = data.configKeySaved;
          this.openBtnState = ClrLoadingState.DEFAULT;
          this.inboxOpened = true;
          this.openingInbox = false;
        },
          error => {
            this.error = error;
            this.openingInbox = false;
            this.openBtnState = ClrLoadingState.DEFAULT;
          });
    }
  }
}
