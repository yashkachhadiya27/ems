import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeConvert',
  standalone: true,
})
export class TimeConvertPipe implements PipeTransform {
  transform(value: string): string {
    var time = value.split('.')[0].split('T')[1];
    return time;
  }
}
