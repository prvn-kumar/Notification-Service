import { Component, OnInit, Input } from '@angular/core';
import { InboxService } from '../inbox.service';

@Component({
  selector: 'inbox',
  templateUrl: './inbox.component.html',
  styleUrls: ['./inbox.component.scss']
})
export class InboxComponent implements OnInit {
  @Input() inboxName: string;
  constructor(private inboxService: InboxService) { }

  ngOnInit(): void {
    console.log("inbox called");
  }

}
