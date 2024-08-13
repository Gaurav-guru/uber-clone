import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-rating-star-bar',
  templateUrl: './rating-star-bar.component.html',
  styleUrls: ['./rating-star-bar.component.css','../../app.component.css']
})
export class RatingStarBarComponent implements OnInit{
  ratingArr = new Array<number>();
  starCount = 10;
  @Input() rating = 0;
  @Output() ratingUpdated = new EventEmitter<number>();
  ngOnInit() {
    for (let index = 0; index < this.starCount; index++) {
      this.ratingArr.push(index);
    }
  }

  showIcon(index:number) {
    if (this.rating >= index + 1) {
      return 'star';
    } else {
      return 'star_border';
    }
  }

  onClick(rating:number) {
    this.ratingUpdated.emit(rating);
    return false;
  }


}
