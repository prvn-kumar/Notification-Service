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
  error: any;

  constructor(private inboxService: InboxService) { }

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

  isOpenClicked() {
    this.error = null;
    this.isOpen = true;
  }

  openInbox() {
    this.error = null;
    this.openBtnState = ClrLoadingState.LOADING;
    this.inboxService.getInbox(this.inboxToOpen)
      .subscribe((data: any) => {
        this.inboxName = data.name;
        this.inboxLocation = data.location;
        this.notificationCount = data.notificationCount;
        this.configKeySaved = data.configKeySaved;
        this.openBtnState = ClrLoadingState.DEFAULT;
        this.inboxOpened = true;
      },
        error => {
          this.error = error;
          this.openBtnState = ClrLoadingState.DEFAULT;
        });
  }
}
