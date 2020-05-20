import { Component, Input, OnInit } from '@angular/core';
import { ClrLoadingState } from '@clr/angular';
import { InboxService } from '../inbox.service';
import { Subscription } from 'rxjs';

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
  keySaved: boolean;
  notifications: any;
  saveBtnState: ClrLoadingState = ClrLoadingState.DEFAULT;
  constructor(private inboxService: InboxService) {
    console.log("inbox component created...");
  }

  ngOnInit(): void {
    console.log("inbox called with " + this.inboxName + " & location: " + this.inboxLocation);
    this.getInbox();
    this.refreshSub = this.inboxService.refreshObservable$.subscribe(() => {
      console.log("refreshing inbox...")
      this.getInboxNotifications();
    });
  }

  ngOnDestroy(): void {
    this.refreshSub.unsubscribe();
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
      },
        error => {
          this.error = error;
        });
  }
}
