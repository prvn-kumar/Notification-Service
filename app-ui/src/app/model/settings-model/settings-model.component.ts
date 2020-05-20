import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-settings-model',
  templateUrl: './settings-model.component.html',
  styleUrls: ['./settings-model.component.scss']
})
export class SettingsModelComponent implements OnInit, OnChanges {
  @Input() openModel: boolean;
  @Input() modelSetting: string;
  constructor() { }

  ngOnChanges(changes: SimpleChanges): void {
    let chng = changes['openModel'];
    if (chng) {
      this.openModel = true;
    }
  }

  ngOnInit(): void {

  }

  cancelSetting() {
    this.openModel = false;
  }

  saveSetting() {
    this.openModel = false;
  }
}
