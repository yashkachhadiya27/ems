// highcharts.d.ts
import * as Highcharts from 'highcharts';

declare module 'highcharts' {
  interface Chart {
    customLabel?: Highcharts.SVGElement; // Define customLabel as an optional property of type SVGElement
  }
}
