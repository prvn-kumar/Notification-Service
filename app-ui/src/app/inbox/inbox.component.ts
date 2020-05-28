import { Component, Input, OnInit } from '@angular/core';
import { ClrLoadingState } from '@clr/angular';
import { InboxService } from '../inbox.service';
import { Subscription } from 'rxjs';
import { saveAs } from 'file-saver';

@Component({
  selector: 'inbox',
  templateUrl: './inbox.component.html',
  styleUrls: ['./inbox.component.scss']
})
export class InboxComponent implements OnInit {
  @Input() inboxName: string;
  @Input() inboxLocation: string;
  refreshSub: Subscription;
  error: any;
  key: string;
  alertMsg: string;
  alertClosed: boolean;
  keySaved: boolean;
  notifications: any;
  saveBtnState: ClrLoadingState = ClrLoadingState.DEFAULT;
  constructor(private inboxService: InboxService) {
    console.log("inbox component created...");
    this.alertClosed = true;
    this.refreshSub = this.inboxService.refreshObservable$.subscribe(() => {
      console.log("refreshing inbox...")
      this.getInboxNotifications();
    });
  }

  ngOnInit(): void {
    console.log("inbox called with " + this.inboxName + " & location: " + this.inboxLocation);
    this.getInbox();
    this.inboxService.refreshInbox();
  }

  ngOnDestroy(): void {
    this.refreshSub.unsubscribe();
  }

  downloadNotification(notification: any): void {
    let filename = notification.id;
    if (notification && notification.decryptedContent && notification.decryptedContent.payload && notification.decryptedContent.payload.id) {
      filename = notification.decryptedContent.payload.id;
    }
    const data = notification.decryptedContent ? notification.decryptedContent : notification;
    console.log(filename, data);
    var blob = new Blob([JSON.stringify(data)], { type: "text/json;charset=utf-8" });
    saveAs(blob, filename + ".json");
  }

  saveKey() {
    this.error = null;
    this.saveBtnState = ClrLoadingState.LOADING;
    console.log("getting inbox notifications.." + this.inboxName)
    this.inboxService.saveConfigKey(this.inboxName, this.key)
      .subscribe((data: any) => {
        this.notifications = data;
        this.keySaved = true;
        this.saveBtnState = ClrLoadingState.DEFAULT;
      },
        error => {
          this.error = error;
          this.saveBtnState = ClrLoadingState.DEFAULT;
        });
  }

  getInbox() {
    this.error = null;
    this.inboxService.getInbox(this.inboxName)
      .subscribe((data: any) => {
        this.inboxLocation = data.location;
        this.keySaved = data.configKeySaved;
      },
        error => {
          this.error = error;
        });
  }

  getInboxNotifications() {
    this.error = null;
    console.log("getting inbox notifications.." + this.inboxName)
    this.inboxService.getInboxNotifications(this.inboxName)
      .subscribe((data: any) => {
        this.notifications = data;
        if (this.notifications && this.notifications.length > 0) {
          this.alertClosed = true;
        } else {
          this.alertClosed = false;
          this.alertMsg = "Inbox empty. Configure Webhook setup..."
        }
      },
        error => {
          this.error = error;
          this.notifications = [];
        });
  }
}
