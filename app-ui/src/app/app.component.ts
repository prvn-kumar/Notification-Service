import { Component } from '@angular/core';
import { ClrLoadingState } from '@clr/angular';
import { InboxService } from './inbox.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  inboxName: string;
  title = 'ACI Webhook App';
  inboxOpened: boolean;
  submitBtnState: ClrLoadingState = ClrLoadingState.DEFAULT;
  error: any;

  constructor(private inboxService: InboxService) { }

  createInbox() {
    this.error = null;
    this.submitBtnState = ClrLoadingState.LOADING;
    this.inboxService.createInbox()
      .subscribe((data: any) => {
        this.inboxName = data.name;
        this.submitBtnState = ClrLoadingState.DEFAULT;
        this.inboxOpened = true;
      },
        error => {
          this.error = error;
          this.submitBtnState = ClrLoadingState.DEFAULT;
        });
  }

  openInbox() {
    this.inboxOpened = true;
  }
}
